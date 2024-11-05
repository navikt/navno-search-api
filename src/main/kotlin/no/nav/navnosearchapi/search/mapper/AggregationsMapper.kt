package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.Aggregations
import no.nav.navnosearchapi.search.dto.FacetBucket
import no.nav.navnosearchapi.search.dto.UnderAggregations
import no.nav.navnosearchapi.search.filters.facets.fasettFilters
import org.springframework.stereotype.Component

@Component
class AggregationsMapper {
    fun toAggregations(aggregations: Map<String, Long>, params: Params): Aggregations {
        fun facetAggregation(key: String, name: String, underFacetAggregations: List<FacetBucket>? = null) =
            FacetBucket(
                key = key,
                name = name,
                docCount = aggregations[name] ?: 0,
                checked = key == params.f,
                underaggregeringer = underFacetAggregations?.let { UnderAggregations(it) } ?: UnderAggregations()
            )

        fun underFacetAggregation(key: String, name: String, aggregationName: String = name) = FacetBucket(
            key = key,
            name = name,
            docCount = aggregations[aggregationName] ?: 0,
            checked = key in params.uf,
        )

        return Aggregations(
            fasetter = UnderAggregations(
                buckets = fasettFilters.map { facet ->
                    facetAggregation(
                        key = facet.key,
                        name = facet.name,
                        underFacetAggregations = facet.underFacets.map { underFacet ->
                            underFacetAggregation(
                                key = underFacet.key,
                                name = underFacet.name,
                                aggregationName = underFacet.aggregationName
                            )
                        }.removeEmpty()
                    )
                }
            )
        )
    }

    private fun List<FacetBucket>.removeEmpty(): List<FacetBucket> {
        return filter { b -> b.docCount > 0 }
    }
}