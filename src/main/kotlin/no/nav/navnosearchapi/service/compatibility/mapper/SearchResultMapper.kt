package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.Aggregations
import no.nav.navnosearchapi.service.compatibility.dto.Bucket
import no.nav.navnosearchapi.service.compatibility.dto.FacetBucket
import no.nav.navnosearchapi.service.compatibility.dto.SearchHit
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.dto.UnderAggregations
import no.nav.navnosearchapi.service.compatibility.utils.AggregationNames
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.FacetNames
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetNames
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import org.springframework.stereotype.Component

@Component
class SearchResultMapper {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return SearchResult(
            page = params.page,
            s = params.s,
            preferredLanguage = params.preferredLanguage,
            isMore = result.totalPages > (result.pageNumber + 1),
            word = params.ord,
            total = result.totalElements,
            fasettKey = params.f,
            aggregations = toAggregations(result.aggregations!!, params),
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
        } else if (facet in innholdFacets) {
            toIngressHighlight(searchHit.highlight.ingress.firstOrNull() ?: searchHit.ingress)
        } else if (facet == FacetKeys.STATISTIKK && searchHit.ingress.isBlank()) {
            TABELL
        } else {
            searchHit.highlight.ingress.firstOrNull()?.let { toIngressHighlight(it) }
                ?: searchHit.highlight.text.firstOrNull()?.let { toTextHighlight(it) }
                ?: toIngressHighlight(searchHit.ingress)
        }
    }

    private fun toTextHighlight(highlight: String): String {
        return highlight + CUTOFF_POSTFIX
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
                        key = FacetKeys.PRIVATPERSON,
                        name = FacetNames.PRIVATPERSON,
                        docCount = aggregations[FacetNames.PRIVATPERSON] ?: 0,
                        checked = FacetKeys.PRIVATPERSON == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UnderFacetKeys.INFORMASJON,
                                    name = UnderFacetNames.INFORMASJON,
                                    docCount = aggregations[AggregationNames.PRIVATPERSON_INFORMASJON] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.INFORMASJON),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.KONTOR,
                                    name = UnderFacetNames.KONTOR,
                                    docCount = aggregations[AggregationNames.PRIVATPERSON_KONTOR] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.KONTOR),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.SOKNAD_OG_SKJEMA,
                                    name = UnderFacetNames.SOKNAD_OG_SKJEMA,
                                    docCount = aggregations[AggregationNames.PRIVATPERSON_SOKNAD_OG_SKJEMA] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.SOKNAD_OG_SKJEMA),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.AKTUELT,
                                    name = UnderFacetNames.AKTUELT,
                                    docCount = aggregations[AggregationNames.PRIVATPERSON_AKTUELT] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.AKTUELT),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FacetKeys.ARBEIDSGIVER,
                        name = FacetNames.ARBEIDSGIVER,
                        docCount = aggregations[FacetNames.ARBEIDSGIVER] ?: 0,
                        checked = FacetKeys.ARBEIDSGIVER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UnderFacetKeys.INFORMASJON,
                                    name = UnderFacetNames.INFORMASJON,
                                    docCount = aggregations[AggregationNames.ARBEIDSGIVER_INFORMASJON] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.INFORMASJON),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.KONTOR,
                                    name = UnderFacetNames.KONTOR,
                                    docCount = aggregations[AggregationNames.ARBEIDSGIVER_KONTOR] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.KONTOR),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.SOKNAD_OG_SKJEMA,
                                    name = UnderFacetNames.SOKNAD_OG_SKJEMA,
                                    docCount = aggregations[AggregationNames.ARBEIDSGIVER_SOKNAD_OG_SKJEMA] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.SOKNAD_OG_SKJEMA),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.AKTUELT,
                                    name = UnderFacetNames.AKTUELT,
                                    docCount = aggregations[AggregationNames.ARBEIDSGIVER_AKTUELT] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.AKTUELT),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FacetKeys.SAMARBEIDSPARTNER,
                        name = FacetNames.SAMARBEIDSPARTNER,
                        docCount = aggregations[FacetNames.SAMARBEIDSPARTNER] ?: 0,
                        checked = FacetKeys.SAMARBEIDSPARTNER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UnderFacetKeys.INFORMASJON,
                                    name = UnderFacetNames.INFORMASJON,
                                    docCount = aggregations[AggregationNames.SAMARBEIDSPARTNER_INFORMASJON] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.INFORMASJON),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.KONTOR,
                                    name = UnderFacetNames.KONTOR,
                                    docCount = aggregations[AggregationNames.SAMARBEIDSPARTNER_KONTOR] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.KONTOR),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.SOKNAD_OG_SKJEMA,
                                    name = UnderFacetNames.SOKNAD_OG_SKJEMA,
                                    docCount = aggregations[AggregationNames.SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.SOKNAD_OG_SKJEMA),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.AKTUELT,
                                    name = UnderFacetNames.AKTUELT,
                                    docCount = aggregations[AggregationNames.SAMARBEIDSPARTNER_AKTUELT] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.AKTUELT),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FacetKeys.NYHETER,
                        name = FacetNames.NYHETER,
                        docCount = aggregations[FacetNames.NYHETER] ?: 0,
                        checked = FacetKeys.NYHETER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UnderFacetKeys.STATISTIKK,
                                    name = UnderFacetNames.STATISTIKK,
                                    docCount = aggregations[AggregationNames.NYHETER_STATISTIKK] ?: 0,
                                    checked = params.uf.contains(UnderFacetNames.STATISTIKK),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.PRESSE,
                                    name = UnderFacetNames.PRESSE,
                                    docCount = aggregations[UnderFacetNames.PRESSE] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.PRESSE),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.NAV_OG_SAMFUNN,
                                    name = UnderFacetNames.NAV_OG_SAMFUNN,
                                    docCount = aggregations[UnderFacetNames.NAV_OG_SAMFUNN] ?: 0,
                                    checked = params.uf.contains(UnderFacetNames.NAV_OG_SAMFUNN),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FacetKeys.ANALYSER_OG_FORSKNING,
                        name = FacetNames.ANALYSER_OG_FORSKNING,
                        docCount = aggregations[FacetNames.ANALYSER_OG_FORSKNING] ?: 0,
                        checked = FacetKeys.ANALYSER_OG_FORSKNING == params.f,
                    ),
                    FacetBucket(
                        key = FacetKeys.STATISTIKK,
                        name = FacetNames.STATISTIKK,
                        docCount = aggregations[FacetNames.STATISTIKK] ?: 0,
                        checked = FacetKeys.STATISTIKK == params.f,
                    ),
                    FacetBucket(
                        key = FacetKeys.INNHOLD_FRA_FYLKER,
                        name = FacetNames.INNHOLD_FRA_FYLKER,
                        docCount = aggregations[FacetNames.INNHOLD_FRA_FYLKER] ?: 0,
                        checked = FacetKeys.INNHOLD_FRA_FYLKER == params.f,
                        underaggregeringer = UnderAggregations(
                            filteredBuckets(
                                FacetBucket(
                                    key = UnderFacetKeys.AGDER,
                                    name = UnderFacetNames.AGDER,
                                    docCount = aggregations[UnderFacetNames.AGDER] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.AGDER),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.INNLANDET,
                                    name = UnderFacetNames.INNLANDET,
                                    docCount = aggregations[UnderFacetNames.INNLANDET] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.INNLANDET),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.MORE_OG_ROMSDAL,
                                    name = UnderFacetNames.MORE_OG_ROMSDAL,
                                    docCount = aggregations[UnderFacetNames.MORE_OG_ROMSDAL] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.MORE_OG_ROMSDAL),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.NORDLAND,
                                    name = UnderFacetNames.NORDLAND,
                                    docCount = aggregations[UnderFacetNames.NORDLAND] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.NORDLAND),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.OSLO,
                                    name =  UnderFacetNames.OSLO,
                                    docCount = aggregations[UnderFacetNames.OSLO] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.OSLO),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.ROGALAND,
                                    name =  UnderFacetNames.ROGALAND,
                                    docCount = aggregations[UnderFacetNames.ROGALAND] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.ROGALAND),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.TROMS_OG_FINNMARK,
                                    name =  UnderFacetNames.TROMS_OG_FINNMARK,
                                    docCount = aggregations[UnderFacetNames.TROMS_OG_FINNMARK] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.TROMS_OG_FINNMARK),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.TRONDELAG,
                                    name =  UnderFacetNames.TRONDELAG,
                                    docCount = aggregations[UnderFacetNames.TRONDELAG] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.TRONDELAG),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.VESTFOLD_OG_TELEMARK,
                                    name =  UnderFacetNames.VESTFOLD_OG_TELEMARK,
                                    docCount = aggregations[UnderFacetNames.VESTFOLD_OG_TELEMARK] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.VESTFOLD_OG_TELEMARK),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.VESTLAND,
                                    name =  UnderFacetNames.VESTLAND,
                                    docCount = aggregations[UnderFacetNames.VESTLAND] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.VESTLAND),
                                ),
                                FacetBucket(
                                    key =  UnderFacetKeys.VEST_VIKEN,
                                    name =  UnderFacetNames.VEST_VIKEN,
                                    docCount = aggregations[UnderFacetNames.VEST_VIKEN] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.VEST_VIKEN),
                                ),
                                FacetBucket(
                                    key = UnderFacetKeys.OST_VIKEN,
                                    name = UnderFacetNames.OST_VIKEN,
                                    docCount = aggregations[UnderFacetNames.OST_VIKEN] ?: 0,
                                    checked = params.uf.contains(UnderFacetKeys.OST_VIKEN),
                                ),
                            )
                        ),
                    ),
                )
            )
        )
    }

    private fun <T> filteredBuckets(vararg buckets: T): List<T> where T : Bucket {
        return buckets.filter { b -> b.docCount > 0 }
    }

    companion object {
        private const val HIGHLIGHT_MAX_LENGTH = 220
        private const val CUTOFF_POSTFIX = " (...)"

        private const val TABELL = "Tabell"

        private val innholdFacets = listOf(FacetKeys.PRIVATPERSON, FacetKeys.ARBEIDSGIVER, FacetKeys.SAMARBEIDSPARTNER)
    }
}