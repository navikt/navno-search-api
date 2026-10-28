# Golden suite (temporary)

Black-box tests that pin what the API does over HTTP today, quirks included, so that it can be rewritten in
TypeScript without anyone noticing. The suite starts the real server, points it at a fake OpenSearch, and checks two
things for every request: what the server sent OpenSearch, and what it answered (plus what it logged at WARN or
ERROR). The expected values are golden files, recorded from the Kotlin app.

The rewrite goes in three stages, each finished before the next starts:

1. **Goldens**: this suite, recorded from and passing against the Kotlin app. Nothing in production changes.
2. **TypeScript**: the new app must pass the same suite, unchanged, before it replaces the Kotlin app. Deliberate
   differences go in `spring-defaults.test.ts` only.
3. **Clean-up**: the Kotlin app and everything here that only serves it goes away.

Until stage 2 is done, `src/main` is frozen: a change there means re-recording, and reviewing the golden diff.

## Running

| Command | What it does |
| --- | --- |
| `pnpm test:http:kotlin` | Runs the suite against `build/libs/navno-search-api.jar` (build it with `./gradlew bootJar`), replaying the recorded OpenSearch answers. Needs JDK 21, but no OpenSearch. |
| `pnpm test:http:record` | Records everything again from scratch: needs `RECORD_UPSTREAM` pointing at an OpenSearch with the fixture index. Always run the whole suite when recording. |

Outside CI, Vitest writes a golden that doesn't exist yet instead of failing; set `CI=true` to make a missing golden
fail locally too.

Recording normally runs in GitHub Actions (`.github/workflows/record-goldens.yml`). Add the label `record-goldens`
to a pull request (create the label once if the repository doesn't have it), or use "Run workflow" on a branch once
the workflow is on main. The job:

1. starts a throwaway OpenSearch 2 container inside the runner;
2. runs `GoldenFixtures.kt` (in `src/test/kotlin`), which creates the index from the common library's `Content` class,
   exactly as navno-search-admin-api creates it in prod, and saves the synthetic documents in `GoldenDocuments.kt`;
3. exports that index to `test/fixtures/opensearch/` (`index.json`, `seed.ndjson`), so it can be rebuilt without Kotlin;
4. builds the jar, runs `pnpm test:http:record`, and commits the goldens and recordings to the branch.

It never touches NAIS clusters, Aiven or real data. GitHub holds the pull request's checks on the workflow's commit
until someone approves them ("action required"), so approve or re-run them afterwards.

Locally the same steps work with JDK 21, Docker and `githubPassword` in `~/.gradle/gradle.properties` (README.md).
`GOLDEN_OPENSEARCH_URI` must point at localhost: `GoldenFixtures` deletes the index before it creates it.

`.github/workflows/goldens-kotlin.yml` runs the suite against the Kotlin app on every pull request that touches it,
the app or the build.

## How it works

| File | What it does |
| --- | --- |
| `harness.ts` | Starts the server under test (`SERVER`; only `kotlin` until stage 2) with a prod-like env: `OPEN_SEARCH_URI` at the stub, fake credentials, `TZ=Europe/Oslo`. Waits for the readiness probe. `request()` makes plain `node:http` requests, so paths and headers are sent exactly as written. `stop()` sends SIGTERM. |
| `stub.ts` | An HTTP server that records every request the server sends OpenSearch. Like OpenSearch's security plugin, it answers a request without credentials with a 401 asking for basic auth, so clients that only send credentials when asked work too; wrong credentials fail the test. Tests override answers with `on()`; `Response.error()` resets the connection. Requests nothing answers get a 599, and fail the test. |
| `opensearch.ts` | The fake OpenSearch: answers a search with what a real OpenSearch answered when exactly that request was recorded (`test/fixtures/opensearch/recordings/`). A server that sends anything else finds no recording. What changes on every run and no server passes on (`took`, the node id in shard failures) is recorded as a fixed value, so recording twice gives the same files. |
| `canonical.ts` | How things are written down: JSON with sorted keys, so key order and pretty-printing never count, and with invisible characters (soft hyphens, no-break spaces, U+0085, U+2028 and the like) as `\u` escapes so a diff shows them. The error body's timestamp is masked once its format has been checked. The aggregations and facet filters every search repeats go to `__goldens__/shared/`, named by content. Recordings are written on one line. |
| `golden.ts` | `golden()`: one request, and everything observable about it compared with `__goldens__/<name>.json`. |
| `clients.ts` | Query strings the way navno-search-frontend and nav-dekoratoren build them, so the main goldens describe real traffic. |
| `global-setup.ts` | Clears the recordings before a recording run, and with `GOLDEN_CHECK_UNUSED` fails a run that left recordings unused. |
| `search.test.ts` | `/content/search`: what the frontend sends, every facet and underfacet, search terms, hits and highlights, parameters. |
| `decorator-search.test.ts` | `/content/decorator-search`: what nav-dekoratoren sends, in every context and language. |
| `search-url.test.ts` | `/content/search-url`. |
| `errors.test.ts` | OpenSearch failing (500, 429, 502 with HTML, reset, malformed JSON) and answering unusually (shard failures, missing fields, dates in other formats, `_score: null`). |
| `boot.test.ts` | No OpenSearch calls at startup or from probes, startup with OpenSearch down, and a request in progress during SIGTERM. |
| `internal.test.ts` | The NAIS probes and the Prometheus endpoint. |
| `spring-defaults.test.ts` | What Spring Boot and Tomcat do by default: routing, methods, query decoding, CORS, pretty-printing, header limits. When the framework changes, this is the file that should change; the others pin the app's own behaviour. |
| `fuzz.test.ts` | 1000 seeded random search terms, as hashes of the upstream requests, to pin how odd input becomes a query. |
| `__goldens__/` | The expected values. Not formatted by oxfmt. |
| `QUIRKS.md` | What the recordings show the Kotlin app doing, the deliberate differences, and what is still open for stage 2. |

## Removing it

When the TypeScript app has replaced the Kotlin app and stage 3 is done: delete `test/http/`, `test/fixtures/`,
`src/test/kotlin/no/nav/navnosearchapi/golden/`, the `test:http:*` scripts in `package.json`, and the workflows
`record-goldens.yml` and `goldens-kotlin.yml`. Or keep running it against the TypeScript app until the same
assurances exist as in-process tests.
