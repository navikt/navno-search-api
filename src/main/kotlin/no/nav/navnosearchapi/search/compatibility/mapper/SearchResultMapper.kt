package no.nav.navnosearchapi.search.compatibility.mapper

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.ENGLISH
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
            aggregations = toAggregations(result.aggregations, params, result.totalElements),
            hits = result.hits.map { toHit(it) },
            isInitialResult = false, // todo: fix
        )
    }

    private fun toHit(searchHit: ContentSearchHit): SearchHit {
        return SearchHit(
            displayName = searchHit.content.title,
            href = searchHit.content.href,
            highlight = searchHit.content.ingress, // todo: bruke highlights der det finnes
            modifiedTime = searchHit.content.metadata.lastUpdated.toString(),
            audience = searchHit.content.metadata.audience,
            language = searchHit.content.metadata.language,
        )
    }

    private fun toAggregations(aggregations: ContentAggregations, params: Params, totalElements: Long): Aggregations {
        return Aggregations(
            // Todo: Legge til flere aggregeringer
            fasetter = UnderAggregations(
                buckets = listOf(
                    toFacetBucket("0", "Innhold", aggregations.metatags[ValidMetatags.INNHOLD.descriptor], params.f),
                    toFacetBucket("en", "English", aggregations.language[ENGLISH], params.f),
                    toFacetBucket("1", "Nyheter", aggregations.metatags[ValidMetatags.NYHET.descriptor], params.f),
                    toFacetBucket(
                        "5",
                        "Analyser og forskning",
                        aggregations.metatags[ValidMetatags.ANALYSE.descriptor],
                        params.f
                    ),
                    toFacetBucket(
                        "3",
                        "Statistikk", // todo: Filtered term query? Ingen overlapp mellom denne og nyheter->statistikk
                        aggregations.metatags[ValidMetatags.STATISTIKK.descriptor],
                        params.f
                    ), // Todo: Må ikke inkludere nyheter
                )
            ),
            tidsperiode = DateRange(
                docCount = totalElements,
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

    private fun toFacetBucket(
        key: String,
        name: String,
        aggregation: Long?,
        facetKey: String,
        underAggregations: UnderAggregations? = null
    ): FacetBucket {
        return FacetBucket(
            key = key,
            name = name,
            docCount = aggregation ?: 0,
            checked = key == facetKey,
            underaggregeringer = underAggregations ?: UnderAggregations(buckets = emptyList()),
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