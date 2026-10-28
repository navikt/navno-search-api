import { spawn } from 'node:child_process';
import { existsSync } from 'node:fs';
import http from 'node:http';
import net from 'node:net';
import { fileURLToPath } from 'node:url';
import { AUTHORIZATION, CREDENTIALS, fakeOpenSearch } from './opensearch.ts';
import { type Override, type Stub, startStub } from './stub.ts';

const ROOT = fileURLToPath(new URL('../../', import.meta.url));

export type ServerName = 'kotlin';

/** The server under test, from SERVER (set by the scripts in package.json) */
export const SERVER = (process.env.SERVER ?? 'kotlin') as ServerName;

type Launcher = {
	command: string;
	args: string[];
	env: (port: number) => Record<string, string>;
	/** Why the server can't be started, if it can't */
	missing: () => string | undefined;
};

const KOTLIN_JAR = `${ROOT}build/libs/navno-search-api.jar`;

// How each server is started. These lines are all that should differ between the servers; the tests must not.
const launchers: Record<ServerName, Launcher> = {
	kotlin: {
		command: 'java',
		// Only makes the JIT warm up faster, which doesn't change what the app does
		args: ['-XX:TieredStopAtLevel=1', '-jar', KOTLIN_JAR],
		env: (port) => ({ SERVER_PORT: String(port) }),
		missing: () => (existsSync(KOTLIN_JAR) ? undefined : `${KOTLIN_JAR} is missing. Build it with ./gradlew bootJar.`),
	},
};

// What prod gets from NAIS and the image (Dockerfile), with fake credentials. Prod doesn't set the page sizes either.
const prodEnv = (openSearchUri: string): Record<string, string> => ({
	OPEN_SEARCH_URI: openSearchUri,
	OPEN_SEARCH_USERNAME: CREDENTIALS.username,
	OPEN_SEARCH_PASSWORD: CREDENTIALS.password,
	TZ: 'Europe/Oslo',
});

export type RawResponse = { status: number; headers: http.IncomingHttpHeaders; body: Buffer };

export type Exit = { code: number | null; signal: NodeJS.Signals | null };

/** A WARN or ERROR log line: its level, and its message up to the first " [", where the Kotlin app puts the exception */
export type LogLine = { level: string; message: string };

export type TestServer = {
	origin: string;
	stub: Stub;
	/** A plain node:http request, with the path and headers exactly as given */
	request: (path: string, options?: { method?: string; headers?: Record<string, string> }) => Promise<RawResponse>;
	/** The server's stdout and stderr so far */
	output: () => string;
	/** Waits until the server has written nothing for a moment, so that a request's log lines are all in */
	settle: () => Promise<void>;
	/** The WARN and ERROR lines in output() after the given length */
	logsSince: (length: number) => LogLine[];
	/** SIGTERM, then SIGKILL after 20 s. Resolves with how the process exited, once the stub has closed too. */
	stop: () => Promise<Exit>;
};

type Options = {
	/** Merged over the defaults; undefined removes a variable */
	env?: Record<string, string | undefined>;
	/** Stub overrides in place from the start, and kept by reset() */
	overrides?: Override[];
	/** Gives the server a port nothing listens on as OpenSearch, instead of the stub */
	openSearchDown?: boolean;
};

const READY_TIMEOUT_MS = 90_000;
const QUIET_MS = 75;

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const freePort = () =>
	new Promise<number>((resolve, reject) => {
		const probe = net.createServer();
		probe.once('error', reject);
		probe.listen(0, '127.0.0.1', () => {
			const { port } = probe.address() as net.AddressInfo;
			probe.close(() => resolve(port));
		});
	});

const logLines = (text: string): LogLine[] =>
	text.split('\n').flatMap((line) => {
		try {
			const { level, message } = JSON.parse(line) as { level?: unknown; message?: unknown };
			return (level === 'WARN' || level === 'ERROR') && typeof message === 'string'
				? [{ level, message: message.split(' [')[0] }]
				: [];
		} catch {
			return [];
		}
	});

/**
 * Starts the server under test against a fresh stub. `ready` resolves once its readiness probe answers 200, and
 * rejects, with the server's output, if the process exits first.
 */
export const launchServer = async ({ env: extraEnv, overrides, openSearchDown = false }: Options = {}) => {
	const launcher = launchers[SERVER];
	const missing = launcher.missing();
	if (missing) {
		throw new Error(missing);
	}

	const stub = await startStub({ fake: fakeOpenSearch, overrides, authorization: AUTHORIZATION });
	const port = await freePort();
	const origin = `http://127.0.0.1:${port}`;
	const openSearchUri = openSearchDown ? `http://127.0.0.1:${await freePort()}` : stub.origin;
	const env = Object.fromEntries(
		Object.entries({
			PATH: process.env.PATH,
			HOME: process.env.HOME,
			JAVA_HOME: process.env.JAVA_HOME,
			...prodEnv(openSearchUri),
			...launcher.env(port),
			...extraEnv,
		}).filter((entry): entry is [string, string] => entry[1] !== undefined),
	);

	const child = spawn(launcher.command, launcher.args, { cwd: ROOT, env, stdio: ['ignore', 'pipe', 'pipe'] });
	// Don't let the server outlive the test run
	const killOnExit = () => child.kill('SIGKILL');
	process.once('exit', killOnExit);

	let output = '';
	child.stdout.on('data', (data) => (output += data));
	child.stderr.on('data', (data) => (output += data));

	let exit: Exit | undefined;
	const exited = new Promise<Exit>((resolve) =>
		child.once('exit', (code, signal) => {
			process.off('exit', killOnExit);
			exit = { code, signal };
			resolve(exit);
		}),
	);

	const server: TestServer = {
		origin,
		stub,
		request: (path, { method = 'GET', headers = {} } = {}) =>
			new Promise<RawResponse>((resolve, reject) => {
				http
					.request({ host: '127.0.0.1', port, path, method, headers, agent: false }, (res) => {
						const chunks: Buffer[] = [];
						res.on('data', (chunk: Buffer) => chunks.push(chunk));
						res.on('end', () =>
							resolve({ status: res.statusCode ?? 0, headers: res.headers, body: Buffer.concat(chunks) }),
						);
					})
					.on('error', reject)
					.end();
			}),
		output: () => output,
		settle: async () => {
			const deadline = Date.now() + 3_000;
			let length = output.length;
			let quietSince = Date.now();
			while (Date.now() - quietSince < QUIET_MS && Date.now() < deadline) {
				await sleep(15);
				if (output.length !== length) {
					length = output.length;
					quietSince = Date.now();
				}
			}
		},
		logsSince: (length) => logLines(output.slice(length)),
		stop: async () => {
			if (!exit) {
				child.kill('SIGTERM');
			}
			const timer = setTimeout(() => child.kill('SIGKILL'), 20_000);
			const result = await exited;
			clearTimeout(timer);
			await stub.close();
			return result;
		},
	};

	const waitForReady = async () => {
		const deadline = Date.now() + READY_TIMEOUT_MS;
		while (!exit) {
			const ready = await server
				.request('/internal/health/readiness')
				.then((res) => res.status === 200)
				.catch(() => false);
			if (ready) {
				return server;
			}
			if (Date.now() > deadline) {
				await server.stop();
				throw new Error(`The server wasn't ready within ${READY_TIMEOUT_MS / 1000} s:\n${output}`);
			}
			await sleep(100);
		}
		await stub.close();
		throw new Error(`The server exited with code ${exit.code} before it was ready:\n${output}`);
	};

	const ready = waitForReady();
	// Callers may await other things first; a failed boot still rejects for them
	ready.catch(() => undefined);
	return { origin, ready };
};

/** launchServer, once ready */
export const startServer = async (options?: Options) => (await launchServer(options)).ready;
