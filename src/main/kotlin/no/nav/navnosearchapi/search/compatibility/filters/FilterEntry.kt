package no.nav.navnosearchapi.search.compatibility.filters

import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder

data class FilterEntry(val name: String, val filters: List<QueryBuilder>) {
    fun toAggregation(additionalFilters: List<QueryBuilder> = emptyList()): FilterAggregationBuilder? {
        return if (additionalFilters.isEmpty()) {
            AggregationBuilders.filter(
                name,
                toBoolQuery(filters)
            )
        } else {
            // For tidsperiode-filtre
            AggregationBuilders.filter(
                name,
                BoolQueryBuilder().must(toBoolQuery(filters)).must(toBoolQuery(additionalFilters))
            )
        }
    }

    private fun toBoolQuery(filters: List<QueryBuilder>): BoolQueryBuilder {
        val boolQuery = BoolQueryBuilder()
        filters.forEach { boolQuery.should(it) }
        return boolQuery
    }
}