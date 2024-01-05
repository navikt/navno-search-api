package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.Aggregations
import no.nav.navnosearchapi.service.compatibility.dto.Bucket
import no.nav.navnosearchapi.service.compatibility.dto.DateRange
import no.nav.navnosearchapi.service.compatibility.dto.DateRangeBucket
import no.nav.navnosearchapi.service.compatibility.dto.FacetBucket
import no.nav.navnosearchapi.service.compatibility.dto.SearchHit
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.dto.UnderAggregations
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ENGLISH_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_FILER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_ALL_DATES
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_12_MONTHS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_7_DAYS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AGDER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ARBEIDSGIVER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ENGLISH_NEWS
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ENGLISH_NEWS_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INNLANDET_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NORDLAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OSLO_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OST_VIKEN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRIVATPERSON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ROGALAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TRONDELAG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTLAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VEST_VIKEN_NAME
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import org.springframework.stereotype.Component

@Component
class SearchResultMapper {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return SearchResult(
            c = params.c,
            s = params.s,
            daterange = params.daterange,
            audience = params.audience,
            preferredLanguage = params.preferredLanguage,
            isMore = result.totalPages > (result.pageNumber + 1),
            word = params.ord,
            total = result.totalElements,
            fasettKey = params.f,
            aggregations = toAggregations(result.aggregations, params),
            hits = result.hits.map { toHit(it, params.f) },
            autoComplete = result.suggestions,
        )
    }

    private fun toHit(searchHit: ContentSearchHit, facet: String): SearchHit {
        return SearchHit(
            displayName = searchHit.title,
            href = searchHit.href,
            highlight = toHighlight(searchHit, facet),
            modifiedTime = searchHit.lastUpdated.toString(),
            audience = searchHit.audience,
            language = searchHit.language,
            type = searchHit.type,
            score = searchHit.score,
        )
    }

    private fun toHighlight(searchHit: ContentSearchHit, facet: String): String {
        return if (isKontor(searchHit)) {
            toIngressHighlight(searchHit.ingress)
        } else if (facet == FASETT_INNHOLD) {
            toIngressHighlight(searchHit.highlight.ingress.firstOrNull() ?: searchHit.ingress)
        } else if (facet == FASETT_STATISTIKK && searchHit.ingress.isBlank()) {
            TABELL
        } else {
            searchHit.highlight.ingress.firstOrNull()?.let { toIngressHighlight(it) }
                ?: searchHit.highlight.text.firstOrNull()?.let { toTextHighlight(it) }
                ?: toIngressHighlight(searchHit.ingress)
        }
    }

    private fun toTextHighlight(highlight: String): String {
        return TEXT_HIGHLIGHT_PREFIX + highlight + TEXT_HIGHLIGHT_POSTFIX
    }

    private fun toIngressHighlight(highlight: String): String {
        return if (highlight.length > HIGHLIGHT_MAX_LENGTH) {
            highlight.substring(0, HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
        } else highlight
    }

    private fun isKontor(searchHit: ContentSearchHit): Boolean {
        return searchHit.type in arrayOf(ValidTypes.KONTOR.descriptor, ValidTypes.KONTOR_LEGACY.descriptor)
    }

    private fun toAggregations(aggregations: Map<String, Long>, params: Params): Aggregations {
        return Aggregations(
            fasetter = UnderAggregations(
                buckets = listOf(
                    FacetBucket(
                        key = FASETT_INNHOLD,
                        name = FASETT_INNHOLD_NAME,
                        docCount = aggregations[FASETT_INNHOLD_NAME] ?: 0,
                        checked = FASETT_INNHOLD == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UNDERFASETT_INFORMASJON,
                                    name = UNDERFASETT_INFORMASJON_NAME,
                                    docCount = aggregations[UNDERFASETT_INFORMASJON_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_INFORMASJON),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_KONTOR,
                                    name = UNDERFASETT_KONTOR_NAME,
                                    docCount = aggregations[UNDERFASETT_KONTOR_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_KONTOR),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_SOKNAD_OG_SKJEMA,
                                    name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME,
                                    docCount = aggregations[UNDERFASETT_SOKNAD_OG_SKJEMA_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_SOKNAD_OG_SKJEMA),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_ENGLISH,
                        name = FASETT_ENGLISH_NAME,
                        docCount = aggregations[FASETT_ENGLISH_NAME] ?: 0,
                        checked = FASETT_ENGLISH == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_NYHETER,
                        name = FASETT_NYHETER_NAME,
                        docCount = aggregations[FASETT_NYHETER_NAME] ?: 0,
                        checked = FASETT_NYHETER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UNDERFASETT_PRIVATPERSON,
                                    name = UNDERFASETT_PRIVATPERSON_NAME,
                                    docCount = aggregations[UNDERFASETT_PRIVATPERSON_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_PRIVATPERSON),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_ARBEIDSGIVER,
                                    name = UNDERFASETT_ARBEIDSGIVER_NAME,
                                    docCount = aggregations[UNDERFASETT_ARBEIDSGIVER_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_ARBEIDSGIVER),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_STATISTIKK,
                                    name = UNDERFASETT_STATISTIKK_NAME,
                                    docCount = aggregations[UNDERFASETT_STATISTIKK_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_STATISTIKK),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_PRESSE,
                                    name = UNDERFASETT_PRESSE_NAME,
                                    docCount = aggregations[UNDERFASETT_PRESSE_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_PRESSE),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_PRESSEMELDINGER,
                                    name = UNDERFASETT_PRESSEMELDINGER_NAME,
                                    docCount = aggregations[UNDERFASETT_PRESSEMELDINGER_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_PRESSEMELDINGER),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_NAV_OG_SAMFUNN,
                                    name = UNDERFASETT_NAV_OG_SAMFUNN_NAME,
                                    docCount = aggregations[UNDERFASETT_NAV_OG_SAMFUNN_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_NAV_OG_SAMFUNN),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_ENGLISH_NEWS,
                                    name = UNDERFASETT_ENGLISH_NEWS_NAME,
                                    docCount = aggregations[UNDERFASETT_ENGLISH_NEWS_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_ENGLISH_NEWS),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_ANALYSER_OG_FORSKNING,
                        name = FASETT_ANALYSER_OG_FORSKNING_NAME,
                        docCount = aggregations[FASETT_ANALYSER_OG_FORSKNING_NAME] ?: 0,
                        checked = FASETT_ANALYSER_OG_FORSKNING == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_STATISTIKK,
                        name = FASETT_STATISTIKK_NAME,
                        docCount = aggregations[FASETT_STATISTIKK_NAME] ?: 0,
                        checked = FASETT_STATISTIKK == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_INNHOLD_FRA_FYLKER,
                        name = FASETT_INNHOLD_FRA_FYLKER_NAME,
                        docCount = aggregations[FASETT_INNHOLD_FRA_FYLKER_NAME] ?: 0,
                        checked = FASETT_INNHOLD_FRA_FYLKER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UNDERFASETT_AGDER,
                                    name = UNDERFASETT_AGDER_NAME,
                                    docCount = aggregations[UNDERFASETT_AGDER_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_AGDER),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_INNLANDET,
                                    name = UNDERFASETT_INNLANDET_NAME,
                                    docCount = aggregations[UNDERFASETT_INNLANDET_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_INNLANDET),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_MORE_OG_ROMSDAL,
                                    name = UNDERFASETT_MORE_OG_ROMSDAL_NAME,
                                    docCount = aggregations[UNDERFASETT_MORE_OG_ROMSDAL_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_MORE_OG_ROMSDAL),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_NORDLAND,
                                    name = UNDERFASETT_NORDLAND_NAME,
                                    docCount = aggregations[UNDERFASETT_NORDLAND_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_NORDLAND),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_OSLO,
                                    name = UNDERFASETT_OSLO_NAME,
                                    docCount = aggregations[UNDERFASETT_OSLO_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_OSLO),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_ROGALAND,
                                    name = UNDERFASETT_ROGALAND_NAME,
                                    docCount = aggregations[UNDERFASETT_ROGALAND_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_ROGALAND),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_TROMS_OG_FINNMARK,
                                    name = UNDERFASETT_TROMS_OG_FINNMARK_NAME,
                                    docCount = aggregations[UNDERFASETT_TROMS_OG_FINNMARK_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_TROMS_OG_FINNMARK),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_TRONDELAG,
                                    name = UNDERFASETT_TRONDELAG_NAME,
                                    docCount = aggregations[UNDERFASETT_TRONDELAG_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_TRONDELAG),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VESTFOLD_OG_TELEMARK,
                                    name = UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME,
                                    docCount = aggregations[UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_VESTFOLD_OG_TELEMARK),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VESTLAND,
                                    name = UNDERFASETT_VESTLAND_NAME,
                                    docCount = aggregations[UNDERFASETT_VESTLAND_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_VESTLAND),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VEST_VIKEN,
                                    name = UNDERFASETT_VEST_VIKEN_NAME,
                                    docCount = aggregations[UNDERFASETT_VEST_VIKEN_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_VEST_VIKEN),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_OST_VIKEN,
                                    name = UNDERFASETT_OST_VIKEN_NAME,
                                    docCount = aggregations[UNDERFASETT_OST_VIKEN_NAME] ?: 0,
                                    checked = params.uf.contains(UNDERFASETT_OST_VIKEN),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_FILER,
                        name = FASETT_FILER_NAME,
                        docCount = aggregations[FASETT_FILER_NAME] ?: 0,
                        checked = FASETT_FILER == params.f,
                    ),
                )
            ),
            tidsperiode = DateRange(
                docCount = aggregations[TIDSPERIODE_ALL_DATES] ?: 0,
                checked = params.daterange == TIDSPERIODE_ALL_DATES.toInt(),
                buckets = listOf(
                    toDateRangeBucket(
                        DATE_RANGE_OLDER_THAN_12_MONTHS,
                        aggregations,
                        params.daterange == TIDSPERIODE_OLDER_THAN_12_MONTHS.toInt()
                    ),
                    toDateRangeBucket(
                        DATE_RANGE_LAST_12_MONTHS,
                        aggregations,
                        params.daterange == TIDSPERIODE_LAST_12_MONTHS.toInt()
                    ),
                    toDateRangeBucket(
                        DATE_RANGE_LAST_30_DAYS,
                        aggregations,
                        params.daterange == TIDSPERIODE_LAST_30_DAYS.toInt()
                    ),
                    toDateRangeBucket(
                        DATE_RANGE_LAST_7_DAYS,
                        aggregations,
                        params.daterange == TIDSPERIODE_LAST_7_DAYS.toInt()
                    ),
                ),
            )
        )
    }

    private fun <T> filteredBuckets(vararg buckets: T): List<T> where T : Bucket {
        return buckets.filter { b -> b.docCount > 0 }
    }

    private fun toDateRangeBucket(key: String, aggregations: Map<String, Long>, checked: Boolean): DateRangeBucket {
        return DateRangeBucket(
            key = key,
            docCount = aggregations[key] ?: 0,
            checked = checked,
        )
    }

    companion object {
        private const val HIGHLIGHT_MAX_LENGTH = 220
        private const val CUTOFF_POSTFIX = " (...)"
        private const val TEXT_HIGHLIGHT_PREFIX = "<i>\""
        private const val TEXT_HIGHLIGHT_POSTFIX = "\"</i>"

        private const val TABELL = "Tabell"
    }
}