import { defineConfig } from 'oxfmt';

export default defineConfig({
	printWidth: 120,
	useTabs: true,
	tabWidth: 2,
	singleQuote: true,
	trailingComma: 'all',
	ignorePatterns: [
		'pnpm-lock.yaml',
		// The Kotlin app, its build and its deploy files are formatted the way they were
		'src/**',
		'build/**',
		'.gradle/**',
		'gradle/**',
		'.nais/**',
		'.github/**',
		'*.md',
		'vars.yaml',
		'test/fixtures/**',
		'**/__goldens__/**',
	],
});
