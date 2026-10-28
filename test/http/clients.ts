/*
 * Query strings the way the API's two callers build them, so the most important goldens describe real traffic.
 */

type FrontendParams = {
	ord?: string;
	page?: number | string;
	f?: string;
	uf?: string[];
	s?: number | string;
	preferredLanguage?: string;
};

/**
 * navno-search-frontend (src/utils/fetch-search-result.ts): always all six parameters, in this order, with `uf`
 * comma-joined, and the whole URL through encodeURI, which leaves characters such as + & = , # as they are.
 */
export const frontendQuery = ({
	ord = '',
	page = 0,
	f = 'privatperson',
	uf = [],
	s = 0,
	preferredLanguage = 'nb',
}: FrontendParams = {}) =>
	encodeURI(`ord=${ord}&page=${page}&f=${f}&uf=${uf.join(',')}&s=${s}&preferredLanguage=${preferredLanguage}`);

/** nav-dekoratoren (packages/server/src/handlers/search-handler.ts): three parameters, `ord` through encodeURIComponent */
export const decoratorQuery = (ord: string, f = 'privatperson', preferredLanguage = 'nb') =>
	`ord=${encodeURIComponent(ord)}&f=${f}&preferredLanguage=${preferredLanguage}`;
