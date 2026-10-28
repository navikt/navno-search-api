import { afterAll, beforeAll, describe, test } from 'vitest';
import { decoratorQuery } from './clients.ts';
import { golden } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

const decoratorSearch = (name: string, query: string) =>
	golden(server, `decorator-search/${name}`, { path: `/content/decorator-search?${query}` });

describe('as nav-dekoratoren asks', () => {
	test.each([
		['term', decoratorQuery('dagpenger')],
		// Search as you type: the decorator asks from the third letter on
		['prefix-3', decoratorQuery('syk')],
		['prefix-4', decoratorQuery('sykp')],
		['prefix-5', decoratorQuery('sykep')],
		['several-words', decoratorQuery('arbeid og helse')],
		// The decorator sends an empty term instead of one over 100 characters
		['empty', decoratorQuery('')],
		['phrase', decoratorQuery('"nedsatt arbeidsevne"')],
		['form-number', decoratorQuery('NAV 09-35.01')],
		['kontakt', decoratorQuery('kontakt')],
		['special-characters', decoratorQuery('a+b & c#d')],
	])('%s', decoratorSearch);
});

describe('contexts and languages', () => {
	test.each([
		...['arbeidsgiver', 'samarbeidspartner'].map((f) => [`context-${f}`, decoratorQuery('sykepenger', f)]),
		...['nn', 'en', 'se', 'pl', 'uk', 'ru'].map((language) => [
			`language-${language}`,
			decoratorQuery('dagpenger', 'privatperson', language),
		]),
	])('%s', decoratorSearch);
});

describe('parameters', () => {
	test.each([
		['page-is-ignored', `${decoratorQuery('dagpenger')}&page=3`],
		['sorted-by-date', `${decoratorQuery('dagpenger')}&s=1`],
		['underfacet', `${decoratorQuery('sykepenger')}&uf=soknad-og-skjema`],
		['missing-ord', 'f=privatperson&preferredLanguage=nb'],
		['unknown-facet', decoratorQuery('dagpenger', 'finnes-ikke')],
	])('%s', decoratorSearch);
});
