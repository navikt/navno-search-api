package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.model.ContentDao
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
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
        highlightFields: List<HighlightField>
    ): SearchPage<ContentDao> {
        val pageRequest = PageRequest.of(page, pageSize)

        val query = if (filters.isNotEmpty()) {
            filteredQuery(baseQuery, filters)
        } else {
            baseQuery
        }

        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(query)
            .withPageable(pageRequest)
            .withHighlightQuery(highlightQuery(highlightFields))
            .withAggregations(aggregations)
            .build()

        val searchHits = operations.search(searchQuery, ContentDao::class.java)
        return SearchHitSupport.searchPageFor(searchHits, pageRequest)
    }

    fun search(query: QueryBuilder, maxResults: Int = 3): SearchHits<ContentDao> {
        return operations.search(
            NativeSearchQueryBuilder().withQuery(query).withMaxResults(maxResults).build(),
            ContentDao::class.java
        )
    }

    private fun highlightQuery(highlightFields: List<HighlightField>): HighlightQuery {
        return HighlightQuery(
            Highlight(highlightFields),
            ContentDao::class.java
        )
    }
}