package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.mapper.outbound.ContentSearchPageMapper
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.utils.defaultIndexCoordinates
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.HighlightQuery
import org.springframework.data.elasticsearch.core.query.StringQuery
import org.springframework.data.elasticsearch.core.query.highlight.Highlight
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Component

@Component
class SearchHelper(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val operations: ElasticsearchOperations,
    val mapper: ContentSearchPageMapper
) {
    val highlightFields = listOf("name.*", "ingress.*", "text.*").map { HighlightField(it) }

    fun search(query: String, page: Int, highlighting: Boolean = true): ContentSearchPage {
        val pageRequest = PageRequest.of(page, pageSize)
        val searchQuery = StringQuery(query, pageRequest)

        if (highlighting) {
            searchQuery.setHighlightQuery(
                HighlightQuery(
                    Highlight(highlightFields),
                    ContentDao::class.java
                )
            )
        }

        val searchHits = operations.search(searchQuery, ContentDao::class.java, defaultIndexCoordinates())
        val searchPage: SearchPage<ContentDao> = SearchHitSupport.searchPageFor(searchHits, pageRequest)

        return mapper.toContentSearchPage(searchPage)
    }
}