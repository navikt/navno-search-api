package no.nav.navnosearchapi.search.service.search

import no.nav.navnosearchapi.common.model.ContentDao
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.HighlightQuery
import org.springframework.data.elasticsearch.core.query.highlight.Highlight
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Component

@Component
class SearchHelper(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val operations: ElasticsearchOperations,
) {
    fun searchPage(
        baseQuery: QueryBuilder,
        page: Int,
        filters: List<QueryBuilder>,
        aggregations: List<AbstractAggregationBuilder<*>>,
        highlightFields: List<HighlightField>,
        sort: Sort? = null
    ): SearchPage<ContentDao> {
        val pageRequest = PageRequest.of(page, pageSize)

        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(baseQuery)
            .withFilter(filterQuery(filters))
            .withPageable(pageRequest)
            .withHighlightQuery(highlightQuery(highlightFields))
            .withAggregations(aggregations)
            .withTrackTotalHits(true)

        sort?.let { searchQuery.withSort(it) }

        val searchHits = operations.search(searchQuery.build(), ContentDao::class.java)
        return SearchHitSupport.searchPageFor(searchHits, pageRequest)
    }

    fun search(
        baseQuery: QueryBuilder,
        filters: List<QueryBuilder>,
        maxResults: Int = 3,
        collapseField: String? = null
    ): SearchHits<ContentDao> {
        val query = if (filters.isNotEmpty()) {
            filteredQuery(baseQuery, filters)
        } else {
            baseQuery
        }

        val searchQuery = NativeSearchQueryBuilder().withQuery(query).withMaxResults(maxResults)
        collapseField?.let { searchQuery.withCollapseField(it) }

        return operations.search(searchQuery.build(), ContentDao::class.java)
    }

    private fun highlightQuery(highlightFields: List<HighlightField>): HighlightQuery {
        return HighlightQuery(
            Highlight(highlightFields),
            ContentDao::class.java
        )
    }
}