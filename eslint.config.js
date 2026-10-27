import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';

export default tseslint.config(
	{
		ignores: ['**/node_modules/**', 'build/**', '.gradle/**', '**/*.config.js', '**/*.config.ts'],
	},
	eslint.configs.recommended,
	...tseslint.configs.recommended,
	{
		files: ['**/*.ts'],
		rules: {
			'@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_$' }],
			'@typescript-eslint/no-explicit-any': 'off',
		},
	},
);
