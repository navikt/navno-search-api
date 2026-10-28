import { afterAll, beforeAll, expect, test } from 'vitest';
import { golden } from './golden.ts';
import { SERVER, startServer, type TestServer } from './harness.ts';

let server: TestServer;

beforeAll(async () => {
	server = await startServer();
});

afterAll(() => server?.stop());

// The NAIS probes (.nais/nais.yaml): startup, liveness and readiness
test.each([
	['health', '/internal/health'],
	['liveness', '/internal/health/liveness'],
	['readiness', '/internal/health/readiness'],
])('%s', (name, path) => golden(server, `internal/${name}`, { path }));

test('serves Prometheus metrics', async () => {
	const response = await server.request('/internal/prometheus');
	const metrics = response.body.toString('utf-8');

	expect(response.status).toBe(200);
	expect(response.headers['content-type']).toMatch(/^(text\/plain|application\/openmetrics-text)/);
	// The metric .nais/alerts.yaml alerts on. The TypeScript app gets business metrics instead (README.md).
	if (SERVER === 'kotlin') {
		expect(metrics).toMatch(/^http_server_requests_seconds_count\{.*status="200".*\} /m);
	}
});
