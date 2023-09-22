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
import no.nav.navnosearchapi.search.compatibility.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.search.compatibility.FASETT_ENGLISH
import no.nav.navnosearchapi.search.compatibility.FASETT_FILER
import no.nav.navnosearchapi.search.compatibility.FASETT_INNHOLD
import no.nav.navnosearchapi.search.compatibility.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.search.compatibility.FASETT_NYHETER
import no.nav.navnosearchapi.search.compatibility.FASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.Params
import no.nav.navnosearchapi.search.compatibility.TIDSPERIODE_LAST_12_MONTHS
import no.nav.navnosearchapi.search.compatibility.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.search.compatibility.TIDSPERIODE_LAST_7_DAYS
import no.nav.navnosearchapi.search.compatibility.TIDSPERIODE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_AGDER
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_OSLO
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.search.compatibility.UNDERFASETT_VEST_VIKEN
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
            autoComplete = result.suggestions?.firstOrNull(),
        )
    }

    private fun toHit(searchHit: ContentSearchHit): SearchHit {
        return SearchHit(
            displayName = searchHit.content.title,
            href = searchHit.content.href,
            highlight = toHighlight(searchHit),
            modifiedTime = searchHit.content.metadata.lastUpdated.toString(),
            audience = searchHit.content.metadata.audience,
            language = searchHit.content.metadata.language,
        )
    }

    private fun toHighlight(searchHit: ContentSearchHit): String {
        return if (searchHit.content.metadata.metatags?.contains(ValidMetatags.KONTOR.descriptor) == true) {
            searchHit.content.ingress
        } else {
            searchHit.highlight.ingress.firstOrNull()
                ?: searchHit.highlight.text.firstOrNull()
                ?: searchHit.content.ingress
        }
    }

    private fun toAggregations(aggregations: ContentAggregations, params: Params): Aggregations {
        return Aggregations(
            fasetter = UnderAggregations(
                buckets = listOf(
                    FacetBucket(
                        key = FASETT_INNHOLD,
                        name = "Innhold",
                        docCount = aggregations.metatags[ValidMetatags.INFORMASJON.descriptor] ?: 0, //todo: fikse
                        checked = "0" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key =UNDERFASETT_INFORMASJON,
                                    name = "Informasjon",
                                    docCount = aggregations.metatags[ValidMetatags.INFORMASJON.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_KONTOR,
                                    name = "Kontor",
                                    docCount = aggregations.metatags[ValidMetatags.KONTOR.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_SOKNAD_OG_SKJEMA,
                                    name = "Søknad og skjema",
                                    docCount = aggregations.metatags[ValidMetatags.SKJEMA.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_ENGLISH,
                        name = "English",
                        docCount = aggregations.language[ENGLISH] ?: 0,
                        checked = "en" == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_NYHETER,
                        name = "Nyheter",
                        docCount = aggregations.metatags[ValidMetatags.NYHET.descriptor] ?: 0,
                        checked = "1" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key = UNDERFASETT_PRIVATPERSON,
                                    name = "Privatperson",
                                    docCount = aggregations.audience[ValidAudiences.PRIVATPERSON.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_ARBEIDSGIVER,
                                    name = "Arbeidsgiver",
                                    docCount = aggregations.audience[ValidAudiences.ARBEIDSGIVER.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_STATISTIKK,
                                    name = "Statistikk", // todo: må endres
                                    docCount = aggregations.metatags[ValidMetatags.STATISTIKK.descriptor] ?: 0,
                                    checked = params.uf.contains("4"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_PRESSE,
                                    name = "Presse",
                                    docCount = aggregations.metatags[ValidMetatags.PRESSE.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_PRESSEMELDINGER,
                                    name = "Pressemeldinger",
                                    docCount = aggregations.metatags[ValidMetatags.PRESSEMELDING.descriptor] ?: 0,
                                    checked = params.uf.contains("pm"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_NAV_OG_SAMFUNN,
                                    name = "NAV og samfunn",
                                    docCount = aggregations.metatags[ValidMetatags.NAV_OG_SAMFUNN.descriptor] ?: 0,
                                    checked = params.uf.contains("5"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_ANALYSER_OG_FORSKNING,
                        name = "Analyser og forskning",
                        docCount = aggregations.metatags[ValidMetatags.ANALYSE.descriptor] ?: 0,
                        checked = "5" == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_STATISTIKK,
                        name = "Statistikk", // todo: må endres
                        docCount = aggregations.metatags[ValidMetatags.STATISTIKK.descriptor] ?: 0,
                        checked = "3" == params.f,
                    ),
                    FacetBucket(
                        key = FASETT_INNHOLD_FRA_FYLKER,
                        name = "Innhold fra fylker",
                        docCount = aggregations.totalCount - (aggregations.fylke[MISSING_FYLKE] ?: 0),
                        checked = "4" == params.f,
                        underaggregeringer = UnderAggregations(
                            listOf(
                                FacetBucket(
                                    key = UNDERFASETT_AGDER,
                                    name = "Agder",
                                    docCount = aggregations.fylke[ValidFylker.AGDER.descriptor] ?: 0,
                                    checked = params.uf.contains("0"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_INNLANDET,
                                    name = "Innlandet",
                                    docCount = aggregations.fylke[ValidFylker.INNLANDET.descriptor] ?: 0,
                                    checked = params.uf.contains("1"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_MORE_OG_ROMSDAL,
                                    name = "Møre og Romsdal",
                                    docCount = aggregations.fylke[ValidFylker.MORE_OG_ROMSDAL.descriptor] ?: 0,
                                    checked = params.uf.contains("2"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_NORDLAND,
                                    name = "Nordland",
                                    docCount = aggregations.fylke[ValidFylker.NORDLAND.descriptor] ?: 0,
                                    checked = params.uf.contains("3"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_OSLO,
                                    name = "Oslo",
                                    docCount = aggregations.fylke[ValidFylker.OSLO.descriptor] ?: 0,
                                    checked = params.uf.contains("4"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_ROGALAND,
                                    name = "Rogaland",
                                    docCount = aggregations.fylke[ValidFylker.ROGALAND.descriptor] ?: 0,
                                    checked = params.uf.contains("5"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_TROMS_OG_FINNMARK,
                                    name = "Troms og Finnmark",
                                    docCount = aggregations.fylke[ValidFylker.TROMS_OG_FINNMARK.descriptor] ?: 0,
                                    checked = params.uf.contains("6"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_TRONDELAG,
                                    name = "Trøndelag",
                                    docCount = aggregations.fylke[ValidFylker.TRONDELAG.descriptor] ?: 0,
                                    checked = params.uf.contains("7"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VESTFOLD_OG_TELEMARK,
                                    name = "Vestfold og Telemark",
                                    docCount = aggregations.fylke[ValidFylker.VESTFOLD_OG_TELEMARK.descriptor] ?: 0,
                                    checked = params.uf.contains("8"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VESTLAND,
                                    name = "Vestland",
                                    docCount = aggregations.fylke[ValidFylker.VESTLAND.descriptor] ?: 0,
                                    checked = params.uf.contains("9"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_VEST_VIKEN,
                                    name = "Vest-Viken",
                                    docCount = aggregations.fylke[ValidFylker.VEST_VIKEN.descriptor] ?: 0,
                                    checked = params.uf.contains("10"),
                                ),
                                FacetBucket(
                                    key = UNDERFASETT_OST_VIKEN,
                                    name = "Øst-Viken",
                                    docCount = aggregations.fylke[ValidFylker.OST_VIKEN.descriptor] ?: 0,
                                    checked = params.uf.contains("11"),
                                ),
                            )
                        ),
                    ),
                    FacetBucket(
                        key = FASETT_FILER,
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
                    toDateRangeBucket(DATE_RANGE_OLDER_THAN_12_MONTHS, aggregations, params.daterange == TIDSPERIODE_OLDER_THAN_12_MONTHS),
                    toDateRangeBucket(DATE_RANGE_LAST_12_MONTHS, aggregations, params.daterange == TIDSPERIODE_LAST_12_MONTHS),
                    toDateRangeBucket(DATE_RANGE_LAST_30_DAYS, aggregations, params.daterange == TIDSPERIODE_LAST_30_DAYS),
                    toDateRangeBucket(DATE_RANGE_LAST_7_DAYS, aggregations, params.daterange == TIDSPERIODE_LAST_7_DAYS),
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