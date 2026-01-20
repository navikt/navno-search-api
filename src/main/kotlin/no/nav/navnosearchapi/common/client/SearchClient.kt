package no.nav.navnosearchapi.common.client

import no.nav.navnosearchadminapi.common.model.Content
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.data.core.OpenSearchOperations
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class SearchClient(private val ops: OpenSearchOperations) {

    fun search(query: NativeSearchQueryBuilder): SearchHits<Content> = ops.search(query.build(), Content::class.java)

    fun searchForPage(query: NativeSearchQueryBuilder, pageRequest: PageRequest): SearchPage<Content> {
        val result = ops.search(query.withPageable(pageRequest).build(), Content::class.java)
        return SearchHitSupport.searchPageFor(result, pageRequest)
    }
}