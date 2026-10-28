import { existsSync, readdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { SHARED_DIR, UPDATING } from './canonical.ts';
import { RECORDINGS_DIR, USED_LOG } from './opensearch.ts';

/*
 * Runs once around the whole suite. A recording run starts from scratch, so recordings and shared parts that no test
 * needs any more go away. With GOLDEN_CHECK_UNUSED (set where the whole suite runs, as in CI), a run fails if it left
 * recordings unused: that would mean a test no longer sends the request it was recorded with.
 */
export default function setup() {
	if (process.env.RECORD_UPSTREAM) {
		rmSync(RECORDINGS_DIR, { recursive: true, force: true });
		if (UPDATING) {
			rmSync(SHARED_DIR, { recursive: true, force: true });
		}
	}
	writeFileSync(USED_LOG, '');

	return () => {
		if (process.env.GOLDEN_CHECK_UNUSED !== 'true') {
			return;
		}
		const used = new Set(readFileSync(USED_LOG, 'utf-8').split('\n'));
		const unused = existsSync(RECORDINGS_DIR)
			? readdirSync(RECORDINGS_DIR).filter((file) => !used.has(file.replace(/\.json$/, '')))
			: [];
		if (unused.length > 0) {
			throw new Error(`No test used these recordings in test/fixtures/opensearch/recordings/: ${unused.join(', ')}`);
		}
	};
}
