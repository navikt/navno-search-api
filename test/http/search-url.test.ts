import { afterAll, beforeAll, test } from 'vitest';
import { golden } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

// No known caller, but it allows CORS, so it is probably called from a browser. CORS is in spring-defaults.test.ts.
test.each([
	['exact-href', `term=${encodeURIComponent('https://www.nav.no/dagpenger')}`],
	['href-with-typo', `term=${encodeURIComponent('https://www.nav.no/dagpengr')}`],
	['no-hit', `term=${encodeURIComponent('https://www.example.com/finnes-ikke')}`],
	['path-only', 'term=dagpenger'],
	['empty-term', 'term='],
	['missing-term', ''],
	['repeated-term', 'term=dagpenger&term=sykepenger'],
])('%s', (name, query) => golden(server, `search-url/${name}`, { path: `/content/search-url?${query}` }));
