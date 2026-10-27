import { afterAll, beforeAll, expect, test } from 'vitest';
import { canonicalJson, canonicalUpstream, parseBody, sha256 } from './canonical.ts';
import { decoratorQuery } from './clients.ts';
import { startServer, type TestServer } from './harness.ts';
import { EMPTY_SEARCH_RESPONSE, SEARCH } from './opensearch.ts';
import { json, on } from './stub.ts';

/*
 * Seeded random search terms, sent the way nav-dekoratoren sends them. OpenSearch answers every search with no hits,
 * so this pins how odd input becomes a query. Each line keeps a hash of what the server sent upstream rather than the
 * request itself, to keep the golden small: to see a difference, send that one term to both servers.
 */

const COUNT = 1000;

const cp = String.fromCodePoint;

// Pieces that matter to how the API reads a term: form numbers, quotes, the "kontakt" override, Unicode whitespace
const PIECES = [
	'dagpenger',
	'syk',
	'arbeid',
	'kontakt',
	'søknad',
	'NAV ',
	'nav',
	'09',
	'35',
	'01',
	'1',
	'-',
	'.',
	' ',
	'  ',
	'"',
	"'",
	'+',
	'%',
	'&',
	'#',
	'?',
	'=',
	',',
	'*',
	'/',
	'<b>',
	'æøå',
	'ÆØÅ',
	'\t',
	cp(0xa0),
	cp(0x1f),
	cp(0x85),
	cp(0xad),
	cp(0x200b),
	cp(0x2028),
	cp(0xfeff),
	cp(0x1f600),
];

// mulberry32: small, seeded, and the same in every Node version
const random = (() => {
	let seed = 20261025;
	return () => {
		seed = (seed + 0x6d2b79f5) | 0;
		let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
		t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
		return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
	};
})();

const terms = Array.from({ length: COUNT }, () =>
	Array.from({ length: Math.floor(random() * 7) }, () => PIECES[Math.floor(random() * PIECES.length)]).join(''),
);

let server: TestServer;

beforeAll(async () => {
	server = await startServer({ overrides: [on(SEARCH, () => json(EMPTY_SEARCH_RESPONSE))] });
});

afterAll(() => server?.stop());

test('turns odd search terms into queries', { timeout: 300_000 }, async () => {
	const lines: string[] = [];
	for (const ord of terms) {
		server.stub.reset();
		const response = await server.request(`/content/decorator-search?${decoratorQuery(ord)}`);
		const body = parseBody(response.body.toString('utf-8')) as { total?: number };
		lines.push(
			JSON.stringify({
				ord,
				status: response.status,
				total: response.status === 200 ? body.total : undefined,
				upstream:
					server.stub.requests.length > 0
						? sha256(canonicalJson(server.stub.requests.map(canonicalUpstream))).slice(0, 16)
						: null,
			}),
		);
	}

	await expect(`${lines.join('\n')}\n`).toMatchFileSnapshot('./__goldens__/fuzz.jsonl');
	expect(server.stub.unexpected).toEqual([]);
});
