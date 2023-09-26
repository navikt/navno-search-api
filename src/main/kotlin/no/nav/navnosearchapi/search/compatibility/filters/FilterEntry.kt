package no.nav.navnosearchapi.search.compatibility.filters

import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder

data class FilterEntry(val name: String, val filters: List<QueryBuilder>) {
    fun toAggregation(additionalFilters: List<QueryBuilder> = emptyList()): FilterAggregationBuilder? {
        return AggregationBuilders.filter( //todo: Funker ikke for tidsperiode-filtre
            name,
            toBoolQuery(filters, additionalFilters)
        )
    }

    private fun toBoolQuery(shouldQueries: List<QueryBuilder>, mustQueries: List<QueryBuilder>): BoolQueryBuilder {
        val boolQuery = BoolQueryBuilder()
        shouldQueries.forEach { boolQuery.should(it) }
        mustQueries.forEach { boolQuery.must(it) }
        return boolQuery
    }
}