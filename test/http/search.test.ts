import { afterAll, beforeAll, describe, test } from 'vitest';
import { frontendQuery } from './clients.ts';
import { golden } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

const search = (name: string, query: string) => golden(server, `search/${name}`, { path: `/content/search?${query}` });

const cp = String.fromCodePoint;

const UNDERFACETS: Record<string, string[]> = {
	privatperson: ['informasjon', 'kontor', 'soknad-og-skjema', 'aktuelt'],
	arbeidsgiver: ['informasjon', 'kontor', 'soknad-og-skjema', 'aktuelt'],
	samarbeidspartner: ['informasjon', 'kontor', 'soknad-og-skjema', 'aktuelt'],
	presse: [],
	statistikk: ['artikler', 'nyheter', 'tabeller'],
	'analyser-og-forskning': ['artikler', 'nyheter'],
	'innhold-fra-fylker': [
		'agder',
		'innlandet',
		'more-og-romsdal',
		'nordland',
		'oslo',
		'rogaland',
		'troms-og-finnmark',
		'trondelag',
		'vestfold-og-telemark',
		'vestland',
		'vest-viken',
		'ost-viken',
	],
};

describe('as navno-search-frontend asks', () => {
	test.each([
		// Every page view starts with this one, and its `uf=` decides whether the facet filter applies at all
		['default-view', frontendQuery()],
		['term', frontendQuery({ ord: 'dagpenger' })],
		['term-sorted-by-date', frontendQuery({ ord: 'dagpenger', s: 1 })],
		['default-view-sorted-by-date', frontendQuery({ s: 1 })],
		['show-more-page-1', frontendQuery({ page: 1 })],
		['show-more-page-2', frontendQuery({ page: 2 })],
		['page-past-the-end', frontendQuery({ ord: 'dagpenger', page: 5 })],
		['underfacets', frontendQuery({ ord: 'sykepenger', uf: ['informasjon', 'soknad-og-skjema'] })],
		['nynorsk', frontendQuery({ ord: 'dagpengar', preferredLanguage: 'nn' })],
		['english', frontendQuery({ ord: 'unemployment', preferredLanguage: 'en' })],
		// Server-side rendering passes on whatever else is in the /sok URL
		['stray-params', `${frontendQuery({ ord: 'dagpenger' })}&utm_source=nav&c=1&start=0&daterange=-1&A`],
		// What is left when a search term with # cuts the URL short
		['cut-at-hash', 'ord=foo'],
	])('%s', search);
});

describe('facets', () => {
	test.each(Object.keys(UNDERFACETS).map((f) => [f, frontendQuery({ f })]))('%s', (f, query) =>
		search(`facet-${f}`, query),
	);
});

describe('underfacets', () => {
	test.each(
		Object.entries(UNDERFACETS).flatMap(([f, underfacets]) =>
			underfacets.map((uf) => [`${f}-${uf}`, frontendQuery({ f, uf: [uf] })]),
		),
	)('%s', (name, query) => search(`underfacet-${name}`, query));
});

describe('search terms', () => {
	test.each([
		['blank', frontendQuery({ ord: '  ' })],
		['two-letters', frontendQuery({ ord: 'sy' })],
		['three-letters', frontendQuery({ ord: 'syk' })],
		['several-words', frontendQuery({ ord: 'arbeid og helse' })],
		['double-space', frontendQuery({ ord: 'arbeid  helse' })],
		['phrase', frontendQuery({ ord: '"nedsatt arbeidsevne"' })],
		['lone-quote', frontendQuery({ ord: '"' })],
		['empty-quotes', frontendQuery({ ord: '""' })],
		['kontakt', frontendQuery({ ord: 'kontakt' })],
		['kontakt-padded', frontendQuery({ ord: ' kontakt ' })],
		['kontakt-capitalized', frontendQuery({ ord: 'Kontakt' })],
		['form-number', frontendQuery({ ord: 'NAV 09-35.01' })],
		['form-number-lowercase', frontendQuery({ ord: 'nav 09-35.01' })],
		['form-number-digits', frontendQuery({ ord: '093501' })],
		['form-number-spaced', frontendQuery({ ord: '09 35 01' })],
		['form-number-with-word', frontendQuery({ ord: 'søknad 093501' })],
		['form-number-twice', frontendQuery({ ord: '093501 093501' })],
		['form-number-next-line', frontendQuery({ ord: `09${cp(0x85)}35${cp(0x85)}01` })],
		['nine-digits', frontendQuery({ ord: '111111111' })],
		['punctuation', frontendQuery({ ord: "=?/:@,;$!'()*" })],
		['plus', frontendQuery({ ord: 'a+b' })],
		['percent', frontendQuery({ ord: '100%' })],
		['angle-brackets', frontendQuery({ ord: '<b>dagpenger</b>' })],
		['norwegian-letters', frontendQuery({ ord: 'søknad æøå ÆØÅ' })],
		['emoji', frontendQuery({ ord: cp(0x1f600) })],
		['no-break-spaces', frontendQuery({ ord: `${cp(0xa0)}dagpenger${cp(0xa0)}` })],
		['unit-separator', frontendQuery({ ord: `${cp(0x1f)}dagpenger` })],
		['byte-order-mark', frontendQuery({ ord: `${cp(0xfeff)}dagpenger` })],
		['100-characters', frontendQuery({ ord: 'dagpenger '.repeat(10) })],
		['1000-characters', frontendQuery({ ord: 'dagpenger '.repeat(100) })],
	])('%s', search);
});

describe('hits and highlights', () => {
	test.each([
		['long-ingress-late-match', frontendQuery({ ord: 'meldekort' })],
		['text-only-match', frontendQuery({ ord: 'skattetrekk' })],
		['title-only-match', frontendQuery({ ord: 'tilleggsstønader' })],
		['soft-hyphen', frontendQuery({ ord: 'grunnstønad' })],
		['markup-in-content', frontendQuery({ ord: 'hjelpemidler' })],
		['emoji-at-the-cut', frontendQuery({ ord: 'emojitest', f: 'samarbeidspartner' })],
		['tabell', frontendQuery({ ord: 'ledige', f: 'statistikk' })],
		['provider-subaudiences', frontendQuery({ ord: 'informasjon', f: 'samarbeidspartner' })],
		['news', frontendQuery({ ord: 'nyttår', uf: ['aktuelt'] })],
		['news-updated', frontendQuery({ ord: 'satser', uf: ['aktuelt'] })],
		['press-release', frontendQuery({ ord: 'ledigheten', f: 'presse' })],
		['fylke', frontendQuery({ ord: 'arbeidsmarkedet', f: 'innhold-fra-fylker' })],
		['english-version-hidden', frontendQuery({ ord: 'unemployment' })],
		['other-language', frontendQuery({ ord: 'bargguhisvuođadoarja', preferredLanguage: 'se' })],
		['keyword', frontendQuery({ ord: 'telefon' })],
	])('%s', search);
});

describe('parameters', () => {
	test.each([
		['missing-ord', 'page=0&f=privatperson'],
		['page-not-a-number', frontendQuery({ page: 'abc' })],
		['page-negative', frontendQuery({ page: -1 })],
		['page-decimal', frontendQuery({ page: '1.5' })],
		['page-leading-space', 'ord=&page=%202'],
		['page-plus-sign', 'ord=&page=%2B1'],
		['page-hex', frontendQuery({ page: '0x1' })],
		['page-empty', frontendQuery({ page: '' })],
		['page-past-result-window', frontendQuery({ page: 500 })],
		['page-int-overflow', frontendQuery({ page: 214748365 })],
		['page-too-big-for-int', frontendQuery({ page: '99999999999' })],
		['sort-not-a-number', frontendQuery({ s: 'abc' })],
		['sort-2', frontendQuery({ s: 2 })],
		['repeated-ord', 'ord=dagpenger&ord=sykepenger'],
		['repeated-page', 'ord=&page=1&page=2'],
		['uf-repeated', 'ord=sykepenger&f=privatperson&uf=informasjon&uf=soknad-og-skjema'],
		['uf-with-spaces', frontendQuery({ uf: ['informasjon', ' kontor'] })],
		['uf-trailing-comma', 'ord=&f=privatperson&uf=informasjon,'],
		['uf-unknown', frontendQuery({ uf: ['finnes-ikke'] })],
		['uf-on-facet-without-underfacets', frontendQuery({ f: 'presse', uf: ['informasjon'] })],
		['preferred-language-empty', frontendQuery({ ord: 'dagpenger', preferredLanguage: '' })],
		['preferred-language-missing', 'ord=dagpenger&f=privatperson'],
		['preferred-language-se', frontendQuery({ ord: 'dagpenger', preferredLanguage: 'se' })],
		// Old links to /sok, e.g. XP's news list, still carry the numbered facets
		['legacy-facet-0', frontendQuery({ f: '0' })],
		['legacy-facet-1', frontendQuery({ f: '1', s: 1 })],
		['legacy-facet-en', frontendQuery({ f: 'en' })],
		['facet-empty', frontendQuery({ f: '' })],
	])('%s', search);
});
