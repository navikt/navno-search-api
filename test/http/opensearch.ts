import { appendFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { canonicalJson, canonicalUpstream, compactJson, parseBody, sha256 } from './canonical.ts';
import { routeOf, type StubRequest } from './stub.ts';

/*
 * A fake OpenSearch. It answers a search with what a real OpenSearch answered when exactly that request was recorded
 * (test/fixtures/opensearch/recordings/), and leaves any other request unanswered. With RECORD_UPSTREAM set it
 * records instead, by forwarding searches there. Recording only ever runs against the Kotlin app, so a server that
 * sends OpenSearch anything Kotlin didn't finds no recording, and its test fails.
 */

export const INDEX = 'search-content-v10';

/** The one route the servers use */
export const SEARCH = `POST /${INDEX}/_search`;

/** What harness.ts gives the server as OPEN_SEARCH_USERNAME and OPEN_SEARCH_PASSWORD */
export const CREDENTIALS = { username: 'golden', password: 'golden-password' };

/** What the stub requires of every request (see startStub) */
export const AUTHORIZATION = `Basic ${Buffer.from(`${CREDENTIALS.username}:${CREDENTIALS.password}`).toString('base64')}`;

export const RECORDINGS_DIR = fileURLToPath(new URL('../fixtures/opensearch/recordings/', import.meta.url));

/** Each recording a test uses is noted here, for global-setup.ts to find the ones none did */
export const USED_LOG = fileURLToPath(new URL('./.recordings-used', import.meta.url));

const RECORD_UPSTREAM = process.env.RECORD_UPSTREAM;

// OpenSearch reports deprecations in Warning headers, which the Kotlin client logs, so those are kept too
const KEPT_HEADERS = ['content-type', 'warning'];

export type Recorded = { status: number; headers: Record<string, string>; body: unknown };

type ShardFailure = { node?: string };

/**
 * Gives what changes on every run, and no server passes on, a fixed value: how many milliseconds the search took, and
 * the id OpenSearch gave its node when the container started, which it names in every shard failure
 */
const maskRunDetails = (body: {
	took?: number;
	_shards?: { failures?: ShardFailure[] };
	error?: { failed_shards?: ShardFailure[] };
}) => {
	if ('took' in body) {
		body.took = 0;
	}
	for (const failure of [...(body._shards?.failures ?? []), ...(body.error?.failed_shards ?? [])]) {
		if ('node' in failure) {
			failure.node = 'golden-node';
		}
	}
};

const record = async (request: StubRequest, file: string) => {
	const response = await fetch(`${RECORD_UPSTREAM}${request.path}${request.rawQuery ? `?${request.rawQuery}` : ''}`, {
		method: request.method,
		headers: { 'content-type': request.headers['content-type'] ?? 'application/json' },
		body: request.body || undefined,
	});
	const recorded: Recorded = {
		status: response.status,
		headers: Object.fromEntries(
			KEPT_HEADERS.flatMap((name) => {
				const value = response.headers.get(name);
				return value === null ? [] : [[name, value]];
			}),
		),
		body: parseBody(await response.text()),
	};
	if (typeof recorded.body === 'object' && recorded.body !== null) {
		maskRunDetails(recorded.body);
	}
	mkdirSync(RECORDINGS_DIR, { recursive: true });
	writeFileSync(file, compactJson({ request: canonicalUpstream(request), response: recorded }));
};

/**
 * What OpenSearch answered this exact request, recording it first when RECORD_UPSTREAM is set. A fresh recording is
 * read back from its file too, so the server gets the same bytes whether the suite is recording or replaying.
 */
export const recorded = async (request: StubRequest): Promise<Recorded | undefined> => {
	const key = sha256(canonicalJson(canonicalUpstream(request))).slice(0, 16);
	const file = `${RECORDINGS_DIR}${key}.json`;
	if (!existsSync(file) && RECORD_UPSTREAM) {
		await record(request, file);
	}
	if (!existsSync(file)) {
		return undefined;
	}
	appendFileSync(USED_LOG, `${key}\n`);
	return (JSON.parse(readFileSync(file, 'utf-8')) as { response: Recorded }).response;
};

export const toResponse = ({ status, headers, body }: Recorded) =>
	new Response(typeof body === 'string' ? body : JSON.stringify(body), { status, headers });

export const fakeOpenSearch = async (request: StubRequest) => {
	if (routeOf(request) !== SEARCH) {
		return undefined;
	}
	const answer = await recorded(request);
	if (!answer) {
		throw new Error(`No recording of this search: ${canonicalJson(canonicalUpstream(request))}`);
	}
	return toResponse(answer);
};

/** A valid search response without hits */
export const EMPTY_SEARCH_RESPONSE = {
	took: 1,
	timed_out: false,
	_shards: { total: 1, successful: 1, skipped: 0, failed: 0 },
	hits: { total: { value: 0, relation: 'eq' }, max_score: null, hits: [] },
};
