import { writeFileSync } from 'node:fs';
import { canonicalJson } from '../../http/canonical.ts';

/*
 * Writes the fixture index that GoldenFixtures.kt created, settings, mapping and documents, to this directory, so the
 * same index can be built again without the Kotlin app. Run by .github/workflows/record-goldens.yml:
 *
 *   node test/fixtures/opensearch/export.ts http://localhost:9200
 */

const OPENSEARCH = process.argv[2] ?? 'http://localhost:9200';
const INDEX = 'search-content-v10';

const get = async (path: string): Promise<any> => {
	const response = await fetch(`${OPENSEARCH}${path}`);
	if (!response.ok) {
		throw new Error(`GET ${path}: ${response.status} ${await response.text()}`);
	}
	return response.json();
};

const { settings, mappings } = (await get(`/${INDEX}`))[INDEX];
// Set by OpenSearch when it created the index
for (const name of ['uuid', 'creation_date', 'version', 'provided_name']) {
	delete settings.index[name];
}
writeFileSync(new URL('./index.json', import.meta.url), canonicalJson({ settings, mappings }));

const { hits } = await get(`/${INDEX}/_search?size=10000`);
const documents = (hits.hits as { _id: string; _source: unknown }[]).toSorted((a, b) => (a._id < b._id ? -1 : 1));
writeFileSync(
	new URL('./seed.ndjson', import.meta.url),
	documents.map(({ _id, _source }) => `${JSON.stringify({ index: { _id } })}\n${JSON.stringify(_source)}\n`).join(''),
);

console.log(`Exported the settings, mapping and ${documents.length} documents of ${INDEX}`);
