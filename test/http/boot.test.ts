import { expect, test } from 'vitest';
import { goldenJson } from './canonical.ts';
import { decoratorQuery } from './clients.ts';
import { describeResponse, golden, send } from './golden.ts';
import { startServer, type TestServer } from './harness.ts';
import { EMPTY_SEARCH_RESPONSE, SEARCH } from './opensearch.ts';
import { json, on, routeOf } from './stub.ts';

const PROBES = ['/internal/health', '/internal/health/liveness', '/internal/health/readiness'];

const DECORATOR_SEARCH = `/content/decorator-search?${decoratorQuery('dagpenger')}`;

// Each probe's status, three times over
const probeStatuses = async (server: TestServer) => {
	const statuses: Record<string, unknown[]> = {};
	for (const probe of PROBES) {
		statuses[probe] = [];
		for (let i = 0; i < 3; i++) {
			const response = await send(server, { path: probe });
			statuses[probe].push('error' in response ? response.error : response.status);
		}
	}
	return statuses;
};

test('sends OpenSearch nothing while starting or answering probes', async () => {
	const server = await startServer();
	try {
		const probes = await probeStatuses(server);
		// Nothing has reset the stub, so this is everything since the server started
		const upstream = [...server.stub.challenged, ...server.stub.requests].map(routeOf);
		await expect(goldenJson({ probes, upstream })).toMatchFileSnapshot('./__goldens__/boot/startup.json');
	} finally {
		await server.stop();
	}
});

test('starts and answers probes while OpenSearch is down', async () => {
	const server = await startServer({ openSearchDown: true });
	try {
		await expect(goldenJson({ probes: await probeStatuses(server) })).toMatchFileSnapshot(
			'./__goldens__/boot/opensearch-down-probes.json',
		);
		await golden(server, 'boot/opensearch-down-search', { path: DECORATOR_SEARCH });
	} finally {
		await server.stop();
	}
});

test('finishes a request in progress when it gets SIGTERM', async () => {
	let release = () => {};
	const held = new Promise<void>((resolve) => (release = resolve));
	const server = await startServer({
		overrides: [
			on(SEARCH, async () => {
				await held;
				return json(EMPTY_SEARCH_RESPONSE);
			}),
		],
	});

	const inProgress = send(server, { path: DECORATOR_SEARCH });
	await expect.poll(() => server.stub.requests.length, { timeout: 15_000 }).toBe(1);
	const stopped = server.stop();
	// Long enough for the server to start shutting down before OpenSearch answers
	await new Promise((resolve) => setTimeout(resolve, 500));
	release();

	const response = await inProgress;
	await stopped;
	await expect(goldenJson('error' in response ? response : describeResponse(response, {}))).toMatchFileSnapshot(
		'./__goldens__/boot/request-in-progress-at-sigterm.json',
	);
});
