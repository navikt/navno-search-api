import http from 'node:http';
import type { AddressInfo } from 'node:net';

/*
 * Stands in for OpenSearch. The server under test gets this origin as OPEN_SEARCH_URI, so every request it makes
 * upstream is recorded here. The fake (opensearch.ts) answers unless a test overrides it.
 */

export type StubRequest = {
	method: string;
	path: string;
	/** The query string as sent, without the "?" */
	rawQuery: string;
	query: URLSearchParams;
	headers: Record<string, string | undefined>;
	body: string;
};

/**
 * Answers instead of the fake, or returns undefined to leave the request to it. Response.error() resets the
 * connection. A promise holds the request until it settles.
 */
export type Override = (request: StubRequest) => Response | undefined | Promise<Response | undefined>;

export type Stub = {
	origin: string;
	/** Every request with the right credentials, in the order received */
	requests: StubRequest[];
	/** Requests without credentials, which got a 401 asking for them (see Options.authorization) */
	challenged: StubRequest[];
	/** Requests nothing answered, or whose fake threw, or with the wrong credentials, with why. They got a 599. */
	unexpected: { route: string; query: string; reason: string }[];
	/** Later overrides take precedence, also over the ones the stub was started with */
	override: (override: Override) => void;
	/** Drops both logs and the overrides added with override() */
	reset: () => void;
	close: () => Promise<void>;
};

/** Method and path, e.g. 'POST /search-content-v10/_search' */
export const routeOf = ({ method, path }: StubRequest) => `${method} ${path}`;

/** An override for one route (see routeOf), whatever the query */
export const on =
	(route: string, reply: (request: StubRequest) => ReturnType<Override>): Override =>
	(request) =>
		routeOf(request) === route ? reply(request) : undefined;

/** A JSON response, as OpenSearch sends them */
export const json = (body: unknown, status = 200) =>
	new Response(JSON.stringify(body), { status, headers: { 'content-type': 'application/json; charset=UTF-8' } });

type Options = {
	/** Answers whatever no override does; undefined means nothing does */
	fake: (request: StubRequest) => ReturnType<Override>;
	/** In place from the start, and kept by reset() */
	overrides?: Override[];
	/**
	 * The Authorization header every request must have. Like OpenSearch's security plugin, the stub answers a request
	 * without one with a 401 asking for basic auth, so a client that only sends credentials when asked works too.
	 */
	authorization: string;
};

export const startStub = async ({ fake, overrides = [], authorization }: Options): Promise<Stub> => {
	const requests: StubRequest[] = [];
	const challenged: StubRequest[] = [];
	const unexpected: Stub['unexpected'] = [];
	const added: Override[] = [];

	const answer = async (request: StubRequest) => {
		for (const override of [...overrides, ...added].reverse()) {
			const response = await override(request);
			if (response) {
				return response;
			}
		}
		return fake(request);
	};

	const server = http.createServer(async (incoming, res) => {
		const chunks: Buffer[] = [];
		for await (const chunk of incoming) {
			chunks.push(chunk as Buffer);
		}

		const url = new URL(incoming.url ?? '/', 'http://stub');
		const request: StubRequest = {
			method: incoming.method ?? 'GET',
			path: url.pathname,
			rawQuery: url.search.slice(1),
			query: url.searchParams,
			headers: Object.fromEntries(Object.entries(incoming.headers).map(([name, value]) => [name, value?.toString()])),
			body: Buffer.concat(chunks).toString('utf-8'),
		};

		if (request.headers.authorization === undefined) {
			challenged.push(request);
			res
				.writeHead(401, {
					'www-authenticate': 'Basic realm="OpenSearch Security"',
					'content-type': 'text/plain; charset=UTF-8',
				})
				.end('Unauthorized');
			return;
		}
		if (request.headers.authorization !== authorization) {
			const reason = `Wrong credentials: ${request.headers.authorization}`;
			unexpected.push({ route: routeOf(request), query: request.rawQuery, reason });
			res.writeHead(599, { 'content-type': 'text/plain' }).end(reason);
			return;
		}
		requests.push(request);

		// A failing fake must not leave the request hanging until the server's socket timeout
		const response = await answer(request).catch((error: unknown) => new Error(String(error)));
		if (!response || response instanceof Error) {
			const reason = response?.message ?? `Nothing answers ${routeOf(request)}`;
			unexpected.push({ route: routeOf(request), query: request.rawQuery, reason });
			res.writeHead(599, { 'content-type': 'text/plain' }).end(reason);
			return;
		}
		if (response.type === 'error') {
			res.socket?.destroy();
			return;
		}
		const body = Buffer.from(await response.arrayBuffer());
		res.writeHead(response.status, Object.fromEntries(response.headers)).end(body);
	});

	await new Promise<void>((resolve) => server.listen(0, '127.0.0.1', resolve));
	const { port } = server.address() as AddressInfo;

	return {
		origin: `http://127.0.0.1:${port}`,
		requests,
		challenged,
		unexpected,
		override: (override) => added.push(override),
		reset: () => {
			added.length = 0;
			requests.length = 0;
			challenged.length = 0;
			unexpected.length = 0;
		},
		close: () =>
			new Promise<void>((resolve) => {
				server.closeAllConnections();
				server.close(() => resolve());
			}),
	};
};
