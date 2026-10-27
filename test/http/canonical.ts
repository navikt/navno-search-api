import { createHash } from 'node:crypto';
import { existsSync, mkdirSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import type { StubRequest } from './stub.ts';

/*
 * How requests and responses are written down in the goldens. Only what a client or OpenSearch can tell apart is
 * kept: object keys are sorted and JSON is re-indented, so key order and pretty-printing never show up as differences.
 */

/** Set by `pnpm test:http:record`, which also passes --update */
export const UPDATING = process.env.GOLDEN_UPDATE === 'true';

export const SHARED_DIR = fileURLToPath(new URL('./__goldens__/shared/', import.meta.url));

export const canonicalJson = (value: unknown) => `${JSON.stringify(sortKeys(value), null, 2)}\n`;

const isObject = (value: unknown): value is Record<string, unknown> =>
	typeof value === 'object' && value !== null && !Array.isArray(value);

const sortKeys = (value: unknown): unknown => {
	if (Array.isArray(value)) {
		return value.map(sortKeys);
	}
	if (isObject(value)) {
		return Object.fromEntries(
			Object.keys(value)
				.sort()
				.map((key) => [key, sortKeys(value[key])]),
		);
	}
	return value;
};

export const sha256 = (text: string) => createHash('sha256').update(text).digest('hex');

/** JSON as data, anything else as text */
export const parseBody = (text: string): unknown => {
	try {
		return JSON.parse(text);
	} catch {
		return text;
	}
};

// LocalDateTime: the server's local time with no offset, and as many fraction digits as Java needs (none to nine)
const ERROR_TIMESTAMP = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{1,9})?$/;

/** The error body's timestamp changes with every request, so it's replaced once its format has been checked */
export const maskErrorTimestamp = (body: unknown) =>
	isObject(body) && typeof body.timestamp === 'string' && 'path' in body && ERROR_TIMESTAMP.test(body.timestamp)
		? { ...body, timestamp: '<local date-time>' }
		: body;

export type CanonicalUpstream = {
	method: string;
	path: string;
	params: Record<string, string | string[]>;
	body: unknown;
};

/** A request the server sent upstream, as it matters to OpenSearch: parameter and key order don't */
export const canonicalUpstream = ({ method, path, query, body }: StubRequest): CanonicalUpstream => ({
	method,
	path,
	params: Object.fromEntries(
		[...new Set(query.keys())].sort().map((name) => {
			const values = query.getAll(name);
			return [name, values.length === 1 ? values[0] : values];
		}),
	),
	body: body === '' ? null : parseBody(body),
});

// The parts of a search request that hardly vary between cases, but would make up most of every golden
const SHARED_PARTS = ['aggregations', 'post_filter'];

/**
 * Moves the big, repetitive parts of a search request to files in __goldens__/shared/, named by their content, and
 * leaves a reference in their place. A part no recorded golden has stays inline, so a difference shows in full.
 */
export const shareParts = (upstream: CanonicalUpstream): CanonicalUpstream => {
	if (!isObject(upstream.body)) {
		return upstream;
	}
	const body = { ...upstream.body };
	for (const part of SHARED_PARTS.filter((name) => name in body)) {
		const text = canonicalJson(body[part]);
		const file = `${part}.${sha256(text).slice(0, 12)}.json`;
		if (UPDATING) {
			mkdirSync(SHARED_DIR, { recursive: true });
			writeFileSync(`${SHARED_DIR}${file}`, text);
		}
		if (existsSync(`${SHARED_DIR}${file}`)) {
			body[part] = { $golden: `shared/${file}` };
		}
	}
	return { ...upstream, body };
};
