package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.Aggregations
import no.nav.navnosearchapi.search.dto.FacetBucket
import no.nav.navnosearchapi.search.dto.UnderAggregations
import no.nav.navnosearchapi.search.filters.facets.fasettFilters

fun Map<String, Long>.toAggregations(params: Params) = Aggregations(
    fasetter = UnderAggregations(
        buckets = fasettFilters.map { facet ->
            FacetBucket(
                key = facet.key,
                name = facet.name,
                docCount = this[facet.name] ?: 0,
                checked = facet.key == params.f,
                underaggregeringer = facet.underFacets.map { underFacet ->
                    FacetBucket(
                        key = underFacet.key,
                        name = underFacet.name,
                        docCount = this[underFacet.aggregationName] ?: 0,
                        checked = underFacet.key in params.uf,
                    )
                }.filterNotEmpty().let { UnderAggregations(it) }
            )
        }
    )
)

private fun List<FacetBucket>.filterNotEmpty(): List<FacetBucket> {
    return filter { b -> b.docCount > 0 }
}