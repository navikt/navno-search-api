import { expect } from 'vitest';
import { canonicalUpstream, goldenJson, maskErrorTimestamp, parseBody, shareParts } from './canonical.ts';
import type { RawResponse, TestServer } from './harness.ts';
import type { Override } from './stub.ts';

export type GoldenRequest = { path: string; method?: string; headers?: Record<string, string> };

type Options = {
	/** Stub overrides for this request only */
	overrides?: Override[];
	/** Response headers to write down, besides the media type */
	headers?: string[];
	/** Keep the response body as sent, instead of as data */
	raw?: boolean;
};

// Long header values (the header size test) are described rather than written out
const describeHeaders = (headers: Record<string, string>) =>
	Object.fromEntries(
		Object.entries(headers).map(([name, value]) => [name, value.length > 200 ? `<${value.length} characters>` : value]),
	);

/** A response as the goldens describe it */
export const describeResponse = (response: RawResponse, { headers, raw = false }: Pick<Options, 'headers' | 'raw'>) => {
	const text = response.body.toString('utf-8');
	const type = response.headers['content-type']?.split(';')[0].trim();
	return {
		status: response.status,
		type: type ?? null,
		...(headers && {
			headers: Object.fromEntries(headers.map((header) => [header, response.headers[header]?.toString() ?? null])),
		}),
		body: raw ? text : maskErrorTimestamp(type?.endsWith('json') ? parseBody(text) : text),
	};
};

/** Sends a request, and describes the connection failing as well as any response, so both can go in a golden */
export const send = async (server: TestServer, { path, method = 'GET', headers = {} }: GoldenRequest) => {
	try {
		return await server.request(path, { method, headers });
	} catch (error) {
		return { error: (error as NodeJS.ErrnoException).code ?? String(error) };
	}
};

/**
 * Sends one request and checks everything observable about it against __goldens__/<name>.json: what the server sent
 * OpenSearch, how it answered, and what it logged at WARN or ERROR.
 */
export const golden = async (
	server: TestServer,
	name: string,
	request: GoldenRequest,
	{ overrides = [], headers, raw }: Options = {},
) => {
	server.stub.reset();
	overrides.forEach((override) => server.stub.override(override));
	const outputBefore = server.output().length;

	const response = await send(server, request);
	await server.settle();

	const { path, method = 'GET', headers: requestHeaders = {} } = request;
	const result = {
		request: {
			method,
			path,
			...(Object.keys(requestHeaders).length > 0 && { headers: describeHeaders(requestHeaders) }),
		},
		upstream: server.stub.requests.map((upstream) => shareParts(canonicalUpstream(upstream))),
		response: 'error' in response ? response : describeResponse(response, { headers, raw }),
		logs: server.logsSince(outputBefore),
	};

	await expect(goldenJson(result)).toMatchFileSnapshot(`./__goldens__/${name}.json`);
	expect(server.stub.unexpected).toEqual([]);
};
