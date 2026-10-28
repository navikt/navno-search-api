# What the Kotlin app does, according to the recordings

Recorded from the Kotlin app by the `Record goldens` workflow on navikt/navno-search-api#343: on 27 October 2026, and
again on 28 October with five new cases. Apart from those, the second recording came out byte for byte the same as the
first: the fixture index, the shared parts, every golden, and all recordings but one, which differed only in the id
OpenSearch gives its node at startup. Recording now writes a fixed id instead. `Goldens (Kotlin)` replays the goldens
against a fresh JVM on every pull request, so they reproduce across JVM runs too.

Stage 2 copies everything under "Copy", and changes only what is under "Deliberate differences". Each row names
goldens under `__goldens__/` that show it.

## Copy

| Behaviour | Goldens |
| --- | --- |
| `uf=` (sent by the frontend on every request) means no underfacets, so the facet filter applies: 49 hits, against 78 with no facet filter. | `search/default-view`, `search/uf-unknown` |
| `isMore` is `page + 1 < ceil(total / 20)`. `total` is Spring's `PageImpl` total, which is padded with the page offset when a page lies past the hits: `page=214748365` sends `from: 4` (the int multiplication overflows) and answers `total: 4294967320`. | `search/show-more-page-2`, `search/page-past-the-end`, `search/page-int-overflow` |
| Always seven facet buckets. Underfacet buckets only when they have hits, each with `"underaggregeringer": null`. Presse has `{"buckets": []}`. | `search/default-view` |
| Dates are Java's `ISO_OFFSET_DATE_TIME` of the stored value: the offset kept, `.000` dropped, `.500` written `.5`. The frontend compares these strings. | `search/news-updated`, `search/fylke` |
| `nyhet` gives `publishedTime` and `modifiedTime`, with `modifiedTime` null when both are the same instant. A fylke, or one of presse, pressemelding, analyse or statistikk, gives `modifiedTime` only. Anything else gives neither. | `search/news`, `search/news-updated`, `search/press-release`, `search/fylke`, `search/tabell` |
| With `s=1`, every hit has `"score": "NaN"`, a string. A `_score: null` from OpenSearch gives the same. | `search/term-sorted-by-date`, `errors/answer-score-null` |
| The highlight is the ingress highlight; else the text highlight as `… x …` when the title has no highlight; else the raw ingress. It is cut at 250 UTF-16 units plus ` …`, even inside a `<b>` or a surrogate pair (a lone surrogate is written `\ud83d`, as `JSON.stringify` does too). `tabell` gives `Tabell`. Markup and soft hyphens in the content pass through unescaped. | `search/text-only-match`, `search/title-only-match`, `search/long-ingress-late-match`, `search/emoji-at-the-cut`, `search/tabell`, `search/markup-in-content`, `search/soft-hyphen` |
| `displayName` comes from `title.en` first. `audience` drops `provider` when a `provider_*` audience is there. | `search/english`, `search/provider-subaudiences` |
| `kontakt` (after trimming, case-sensitive) is searched as `kontakt oss`. `word` echoes the raw term. | `search/kontakt-padded`, `search/kontakt-capitalized` |
| Form numbers: the regex's leading `.?` is greedy, so `093501 093501` matches `093501 09`, giving the phrase `NAV 93-01.09` and the remaining term `3501`. Java's `.` doesn't match U+0085, so `09`, NEL, `35`, NEL, `01` is no form number. | `search/form-number-twice`, `search/form-number-next-line`, `search/form-number*` |
| Unknown underfacets, or underfacets on a facet that has none, remove the facet filter altogether. | `search/uf-unknown`, `search/uf-on-facet-without-underfacets` |
| A missing `ord` gives 400 `Påkrevd request parameter mangler: null`. A `page` or `s` that isn't an int (empty, decimal, too big, text) gives 400 `… mangler: page` / `… mangler: s`. `page` accepts ` 2`, `+1` and `0x1`. A repeated `ord` becomes `a,b`; a repeated `page` takes the first value. `uf` is split on commas and trimmed. `!ord=` sets `ord`; `_ord=` counts as missing. | `search/missing-ord`, `search/page-*`, `search/sort-*`, `search/repeated-*`, `search/uf-*`, `spring-defaults/query-default-marker`, `spring-defaults/query-field-marker` |
| 500 `Ukjent feil`, logged at ERROR: an unknown, legacy or empty `f`; `page < 0`; a page past the 10 000-hit window; OpenSearch failing (500, 429, 502 with HTML, reset, malformed JSON); an invalid document (no title, no audience, a date without millis, an epoch-millis date); an entry in `_shards.failures`, with or without `node`, which in practice means any shard failure, since OpenSearch lists failed shards there. | `search/legacy-facet-*`, `search/facet-empty`, `search/page-negative`, `search/page-past-result-window`, `errors/*` |
| `timed_out: true`, unknown fields in a document, and a `_shards.failed` count without entries in `_shards.failures` (the Java client counts the entries), are ignored. | `errors/answer-timed-out`, `errors/answer-hit-with-unknown-field`, `errors/answer-shard-failure-count-only` |
| decorator-search answers 5 hits, ignores `page`, and honours `s` and `uf`. Languages without a rule of their own (se, pl, uk, ru) only hide documents translated into them. | `decorator-search/*` |
| search-url asks for `size: 10` and matches the whole `href` fuzzily, answering `{"url": null, "title": null}` when nothing matches. CORS: `*` with `vary`; a preflight answers 200 with `GET` and max-age 1800, and a preflight for any other method answers 200 without CORS headers. | `search-url/*`, `spring-defaults/cors-*` |
| The request to OpenSearch: the high-level client's URL parameters, `track_total_hits: 2147483647`, `version: true`, `explain: false`, and every Java builder default. | every golden's `upstream`, `shared/` |
| The probes never call OpenSearch, and answer 200 while it is down. A request in progress at SIGTERM still completes. | `boot/*` |
| HEAD runs the search. | `spring-defaults/head` |

## Predictions the recordings corrected

| Predicted | Recorded |
| --- | --- |
| An empty `page=` binds as 0 | 400 `… mangler: page` |
| A parameter with invalid %-encoding or UTF-8 is dropped | 500 `Ukjent feil` |
| An epoch-millis date is parsed | 500 |
| Shard failures are ignored | 500 for any entry in `_shards.failures`; a `failed` count alone is ignored |
| search-url asks for up to 10 000 hits | `size: 10`, Spring Data's default |
| A CORS preflight for POST gets 403 | 200 without CORS headers |
| `uf[]=` and `uf[0]=` reach Spring's binder | Tomcat rejects the raw brackets with 400 first |
| A repeated form number is removed everywhere | The greedy `.?` matches across both numbers first (see above) |

## Deliberate differences

Decided before stage 1. The TypeScript side of each goes in `spring-defaults.test.ts` in stage 2; the Kotlin goldens
stay as they are.

| | Kotlin | TypeScript | Goldens |
| --- | --- | --- | --- |
| D1 | An unknown path, `/`, `/favicon.ico`, a trailing slash, wrong case, POST or DELETE: 500 `Ukjent feil`, logged at ERROR, so it counts in both the 5xx and the log alerts | 404, or 405 with `Allow: GET`, with the same body shape, logged at WARN | `spring-defaults/unknown-path`, `root`, `favicon`, `trailing-slash`, `wrong-case`, `post`, `delete` |
| D2 | Pretty-printed JSON (`"url" : null`) | Compact JSON | `spring-defaults/pretty-printed` |
| D3 | Tomcat answers 400 with an HTML page, and logs nothing, for raw `\|`, `{`, `}`, `^`, `` ` `` or `"` in the query, an encoded slash, and headers over 8 KB. Matrix parameters are ignored; `!` and `_` parameter prefixes are Spring binder markers. | Node's and Hono's own behaviour | `spring-defaults/query-raw-*`, `encoded-slash`, `request-header-over-8kb`, `matrix-parameter`, `query-default-marker`, `query-field-marker` |
| D4 | Actuator bodies (`groups`, the actuator content type), plain OPTIONS with `Allow: GET,HEAD,OPTIONS`, JVM metrics | `{"status":"UP"}` and business metrics | `internal/*`, `spring-defaults/options` |

## Open for stage 2

**Keep 500, or answer 400, for invalid %-encoding and for an empty parameter name?** Any invalid %-encoding or UTF-8,
and any empty name, gives 500 without asking OpenSearch, wherever it is in the query string, even in a parameter the
app never reads (`spring-defaults/query-invalid-*`, `query-truncated-utf8`, `query-latin1-percent`, `query-empty-name*`).
A name without `=` is fine. Both come from Tomcat and Spring rather than from the app, and neither caller sends them.
Recommendation: 400 with the usual error body, as a fifth deliberate difference.
