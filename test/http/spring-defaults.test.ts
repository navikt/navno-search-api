import { afterAll, beforeAll, describe, test } from 'vitest';
import { decoratorQuery } from './clients.ts';
import { golden, type GoldenRequest } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';

/*
 * What the server does because Spring Boot and Tomcat do it by default, kept here so that changing the framework only
 * changes this file. The other files pin the app's own behaviour. The TypeScript port adds its deliberate differences
 * here (README.md).
 */

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

const HEADERS = [
	'content-type',
	'allow',
	'vary',
	'access-control-allow-origin',
	'access-control-allow-methods',
	'access-control-allow-headers',
	'access-control-allow-credentials',
	'access-control-max-age',
];

const DECORATOR_SEARCH = `/content/decorator-search?${decoratorQuery('dagpenger')}`;

const check = (name: string, request: GoldenRequest) =>
	golden(server, `spring-defaults/${name}`, request, { headers: HEADERS });

describe('routing', () => {
	test.each([
		['unknown-path', { path: '/finnes-ikke' }],
		['root', { path: '/' }],
		['favicon', { path: '/favicon.ico' }],
		['trailing-slash', { path: '/content/decorator-search/?ord=dagpenger' }],
		['wrong-case', { path: '/Content/Decorator-Search?ord=dagpenger' }],
		['matrix-parameter', { path: '/content/decorator-search;x=y?ord=dagpenger' }],
		['encoded-slash', { path: '/content%2Fdecorator-search?ord=dagpenger' }],
		['post', { path: DECORATOR_SEARCH, method: 'POST' }],
		['delete', { path: DECORATOR_SEARCH, method: 'DELETE' }],
		['head', { path: DECORATOR_SEARCH, method: 'HEAD' }],
		['options', { path: DECORATOR_SEARCH, method: 'OPTIONS' }],
	])('%s', check);
});

// How Tomcat and Spring read the query string. The app's own parameter rules are in search.test.ts.
describe('query strings', () => {
	test.each([
		['plus-is-space', 'ord=dagpenger+sykepenger'],
		['encoded-plus', 'ord=a%2Bb'],
		['invalid-percent', 'ord=%'],
		['invalid-percent-pair', 'ord=dag%ZZpenger'],
		['invalid-utf8', 'ord=%FF'],
		['truncated-utf8', 'ord=%C3'],
		['latin1-percent', 'ord=s%F8knad'],
		['raw-pipe', 'ord=a|b'],
		['raw-braces', 'ord={a}'],
		['raw-caret', 'ord=a^b'],
		['raw-backtick', 'ord=a`b'],
		['raw-quote', 'ord="dagpenger"'],
		['default-marker', '!ord=dagpenger'],
		['field-marker', '_ord=dagpenger'],
		['indexed-list', 'ord=sykepenger&uf[0]=soknad-og-skjema'],
		['bracket-list', 'ord=sykepenger&uf[]=informasjon&uf[]=kontor'],
		['empty-name', '=x&ord=dagpenger'],
		['name-only', 'ord'],
		['semicolon-separator', 'ord=dagpenger;f=arbeidsgiver'],
	])('%s', (name, query) => check(`query-${name}`, { path: `/content/decorator-search?${query}` }));
});

describe('CORS', () => {
	const origin = { origin: 'https://www.nav.no' };
	test.each([
		['search-url', { path: '/content/search-url?term=dagpenger', headers: origin }],
		[
			'search-url-preflight',
			{
				path: '/content/search-url?term=dagpenger',
				method: 'OPTIONS',
				headers: { ...origin, 'access-control-request-method': 'GET', 'access-control-request-headers': 'x-custom' },
			},
		],
		[
			'search-url-preflight-for-post',
			{
				path: '/content/search-url?term=dagpenger',
				method: 'OPTIONS',
				headers: { ...origin, 'access-control-request-method': 'POST' },
			},
		],
		['decorator-search', { path: DECORATOR_SEARCH, headers: origin }],
		[
			'decorator-search-preflight',
			{ path: DECORATOR_SEARCH, method: 'OPTIONS', headers: { ...origin, 'access-control-request-method': 'GET' } },
		],
	])('%s', (name, request: GoldenRequest) => check(`cors-${name}`, request));
});

test('pretty-prints JSON', () =>
	golden(server, 'spring-defaults/pretty-printed', { path: '/content/search-url?term=finnes-ikke' }, { raw: true }));

test('rejects request headers over 8 KB', () =>
	check('request-header-over-8kb', { path: DECORATOR_SEARCH, headers: { 'x-large': 'x'.repeat(9000) } }));
