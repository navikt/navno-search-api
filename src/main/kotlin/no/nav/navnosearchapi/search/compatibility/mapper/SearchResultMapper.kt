package no.nav.navnosearchapi.search.compatibility.mapper

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.MISSING_FYLKE
import no.nav.navnosearchapi.search.compatibility.Params
import no.nav.navnosearchapi.search.compatibility.dto.Aggregations
import no.nav.navnosearchapi.search.compatibility.dto.DateRange
import no.nav.navnosearchapi.search.compatibility.dto.DateRangeBucket
import no.nav.navnosearchapi.search.compatibility.dto.FacetBucket
import no.nav.navnosearchapi.search.compatibility.dto.SearchHit
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.compatibility.dto.UnderAggregations
import no.nav.navnosearchapi.search.dto.ContentAggregations
import no.nav.navnosearchapi.search.dto.ContentSearchHit
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import org.springframework.stereotype.Component

@Component
class SearchResultMapper {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return SearchResult(
            c = params.c,
            s = params.s,
            daterange = params.daterange,
            isMore = result.totalPages > (result.pageNumber + 1),
            word = params.ord,
            total = result.totalElements,
            fasettKey = params.f,
            aggregations = toAggregations(result.aggregations, params),
            hits = result.hits.map { toHit(it) },
            isInitialResult = false, // todo: fix,
            autoComplete = result.suggestions.firstOrNull(),
        )
    }

    private fun toHit(searchHit: ContentSearchHit): SearchHit {
        return SearchHit(
            displayName = searchHit.content.title,
            href = searchHit.content.href,
            highlight = searchHit.highlight.ingress.firstOrNull()
                ?: searchHit.highlight.text.firstOrNull()
                ?: searchHit.content.ingress,
            modifiedTime = searchHit.content.metadata.lastUpdated.toString(),
            audience = searchHit.content.metadata.audience,
            language = searchHit.content.metadata.language,
        )
    }

    private fun toAggregations(aggregations: ContentAggregations, params: Params): Aggregations {
        return Aggregations(
            fasetter = UnderAggregations(
                buckets = listOf(
                    FacetBucket(
                        key = "0",
                        name = "Innhold",
                        docCount = aggregations.metatags[ValidMetatags.INNHOLD.descriptor] ?: 0,
                        checked = "0" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key = "0",
                                    name = "Informasjon",
                                    docCount = aggregations.metatags[ValidMetatags.INFORMASJON.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = "1",
                                    name = "Kontor",
                                    docCount = aggregations.metatags[ValidMetatags.KONTOR.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = "2",
                                    name = "Søknad og skjema",
                                    docCount = aggregations.metatags[ValidMetatags.SKJEMA.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = "en",
                        name = "English",
                        docCount = aggregations.language[ENGLISH] ?: 0,
                        checked = "en" == params.f,
                    ),
                    FacetBucket(
                        key = "1",
                        name = "Nyheter",
                        docCount = aggregations.metatags[ValidMetatags.NYHET.descriptor] ?: 0,
                        checked = "1" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key = "1",
                                    name = "Privatperson",
                                    docCount = aggregations.audience[ValidAudiences.PRIVATPERSON.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = "2",
                                    name = "Arbeidsgiver",
                                    docCount = aggregations.audience[ValidAudiences.ARBEIDSGIVER.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                                FacetBucket(
                                    key = "4",
                                    name = "Statistikk", // todo: må endres
                                    docCount = aggregations.metatags[ValidMetatags.STATISTIKK.descriptor] ?: 0,
                                    checked = params.uf.contains("4"),
                                ),
                                FacetBucket(
                                    key = "0",
                                    name = "Presse",
                                    docCount = aggregations.metatags[ValidMetatags.PRESSE.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = "pm",
                                    name = "Pressemeldinger",
                                    docCount = aggregations.metatags[ValidMetatags.PRESSEMELDING.descriptor] ?: 0,
                                    checked = params.uf.contains("pm"),
                                ),
                                FacetBucket(
                                    key = "5",
                                    name = "NAV og samfunn",
                                    docCount = aggregations.metatags[ValidMetatags.NAV_OG_SAMFUNN.descriptor] ?: 0,
                                    checked = params.uf.contains("5"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = "5",
                        name = "Analyser og forskning",
                        docCount = aggregations.metatags[ValidMetatags.ANALYSE.descriptor] ?: 0,
                        checked = "5" == params.f,
                    ),
                    FacetBucket(
                        key = "3",
                        name = "Statistikk", // todo: må endres
                        docCount = aggregations.metatags[ValidMetatags.STATISTIKK.descriptor] ?: 0,
                        checked = "3" == params.f,
                    ),
                    FacetBucket(
                        key = "4",
                        name = "Innhold fra fylker",
                        docCount = aggregations.totalCount - (aggregations.fylke[MISSING_FYLKE] ?: 0),
                        checked = "4" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key = "0",
                                    name = "Agder",
                                    docCount = aggregations.fylke[ValidFylker.AGDER.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = "1",
                                    name = "Innlandet",
                                    docCount = aggregations.fylke[ValidFylker.INNLANDET.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = "2",
                                    name = "Møre og Romsdal",
                                    docCount = aggregations.fylke[ValidFylker.MORE_OG_ROMSDAL.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                                FacetBucket(
                                    key = "3",
                                    name = "Nordland",
                                    docCount = aggregations.fylke[ValidFylker.NORDLAND.descriptor] ?: 0,
                                    checked = params.uf.contains("3"),
                                ),
                                FacetBucket(
                                    key = "4",
                                    name = "Oslo",
                                    docCount = aggregations.fylke[ValidFylker.OSLO.descriptor] ?: 0,
                                    checked = params.uf.contains("4"),
                                ),
                                FacetBucket(
                                    key = "5",
                                    name = "Rogaland",
                                    docCount = aggregations.fylke[ValidFylker.ROGALAND.descriptor] ?: 0,
                                    checked = params.uf.contains("5"),
                                ),
                                FacetBucket(
                                    key = "6",
                                    name = "Troms og Finnmark",
                                    docCount = aggregations.fylke[ValidFylker.TROMS_OG_FINNMARK.descriptor] ?: 0,
                                    checked = params.uf.contains("6"),
                                ),
                                FacetBucket(
                                    key = "7",
                                    name = "Trøndelag",
                                    docCount = aggregations.fylke[ValidFylker.TRONDELAG.descriptor] ?: 0,
                                    checked = params.uf.contains("7"),
                                ),
                                FacetBucket(
                                    key = "8",
                                    name = "Vestfold og Telemark",
                                    docCount = aggregations.fylke[ValidFylker.VESTFOLD_OG_TELEMARK.descriptor] ?: 0,
                                    checked = params.uf.contains("8"),
                                ),
                                FacetBucket(
                                    key = "9",
                                    name = "Vestland",
                                    docCount = aggregations.fylke[ValidFylker.VESTLAND.descriptor] ?: 0,
                                    checked = params.uf.contains("9"),
                                ),
                                FacetBucket(
                                    key = "10",
                                    name = "Vest-Viken",
                                    docCount = aggregations.fylke[ValidFylker.VEST_VIKEN.descriptor] ?: 0,
                                    checked = params.uf.contains("10"),
                                ),
                                FacetBucket(
                                    key = "11",
                                    name = "Øst-Viken",
                                    docCount = aggregations.fylke[ValidFylker.OST_VIKEN.descriptor] ?: 0,
                                    checked = params.uf.contains("11"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = "2",
                        name = "Filer",
                        docCount = aggregations.isFile,
                        checked = "2" == params.f,
                    ),
                )
            ),
            tidsperiode = DateRange(
                docCount = aggregations.totalCount,
                checked = params.daterange == -1,
                buckets = listOf(
                    toDateRangeBucket(DATE_RANGE_OLDER_THAN_12_MONTHS, aggregations, params.daterange == 0),
                    toDateRangeBucket(DATE_RANGE_LAST_12_MONTHS, aggregations, params.daterange == 1),
                    toDateRangeBucket(DATE_RANGE_LAST_30_DAYS, aggregations, params.daterange == 2),
                    toDateRangeBucket(DATE_RANGE_LAST_7_DAYS, aggregations, params.daterange == 3),
                ),
            )
        )
    }

    private fun toDateRangeBucket(key: String, aggregations: ContentAggregations, checked: Boolean): DateRangeBucket {
        return DateRangeBucket(
            key = key,
            docCount = aggregations.dateRangeAggregations[key] ?: 0,
            checked = checked,
        )
    }
}