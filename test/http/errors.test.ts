import { afterAll, beforeAll, describe, test } from 'vitest';
import { decoratorQuery, frontendQuery } from './clients.ts';
import { golden } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';
import { INDEX, recorded, SEARCH, toResponse } from './opensearch.ts';
import { json, on, type Override } from './stub.ts';

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

const DECORATOR_SEARCH = `/content/decorator-search?${decoratorQuery('dagpenger')}`;
const SEARCH_PAGE = `/content/search?${frontendQuery({ ord: 'dagpenger' })}`;

describe('when OpenSearch fails', () => {
	test.each([
		['status-500', () => json({ error: { type: 'exception', reason: 'Something failed' }, status: 500 }, 500)],
		[
			'status-429',
			() => json({ error: { type: 'rejected_execution_exception', reason: 'Too many requests' }, status: 429 }, 429),
		],
		[
			'status-502-html',
			() =>
				new Response('<html><body>502 Bad Gateway</body></html>', {
					status: 502,
					headers: { 'content-type': 'text/html' },
				}),
		],
		['connection-reset', () => Response.error()],
		['malformed-json', () => new Response('{"took":1,', { headers: { 'content-type': 'application/json' } })],
	])('%s', (name, reply: () => Response) =>
		golden(server, `errors/opensearch-${name}`, { path: DECORATOR_SEARCH }, { overrides: [on(SEARCH, reply)] }),
	);
});

// The recorded answer to the request, changed before the server gets it
const changed =
	(change: (body: any) => void): Override =>
	async (request) => {
		const answer = await recorded(request);
		if (!answer) {
			return undefined;
		}
		const body = structuredClone(answer.body);
		change(body);
		return toResponse({ ...answer, body });
	};

describe('when OpenSearch answers unusually', () => {
	test.each([
		[
			'shard-failure',
			(body: any) => {
				body._shards.failed = 1;
				body._shards.failures = [
					{
						shard: 0,
						index: INDEX,
						node: 'golden-node',
						reason: { type: 'query_shard_exception', reason: 'Failed to create query', index: INDEX },
					},
				];
			},
		],
		['timed-out', (body: any) => (body.timed_out = true)],
		['score-null', (body: any) => (body.hits.hits[0]._score = null)],
		['hit-without-title', (body: any) => (body.hits.hits[0]._source.title = {})],
		['hit-without-audience', (body: any) => delete body.hits.hits[0]._source.audience],
		['hit-with-unknown-field', (body: any) => (body.hits.hits[0]._source.unknown = 'x')],
		['date-as-epoch-millis', (body: any) => (body.hits.hits[0]._source.lastUpdated = 1700000000123)],
		['date-without-millis', (body: any) => (body.hits.hits[0]._source.lastUpdated = '2024-01-15T10:30:00+01:00')],
		['date-without-time', (body: any) => (body.hits.hits[0]._source.lastUpdated = '2024-01-15')],
	])('%s', (name, change: (body: any) => void) =>
		golden(server, `errors/answer-${name}`, { path: SEARCH_PAGE }, { overrides: [on(SEARCH, changed(change))] }),
	);
});
