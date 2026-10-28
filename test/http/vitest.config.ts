import { fileURLToPath } from 'node:url';
import { defineConfig } from 'vitest/config';

// The golden suite (README.md), with a config of its own so that it never runs by accident
export default defineConfig({
	test: {
		name: 'http',
		root: fileURLToPath(new URL('../..', import.meta.url)),
		include: ['test/http/**/*.test.ts'],
		globalSetup: ['test/http/global-setup.ts'],
		// One server at a time
		fileParallelism: false,
		// The JVM takes a while to start
		hookTimeout: 120_000,
		testTimeout: 60_000,
	},
});
