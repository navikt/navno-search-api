package no.nav.navnosearchapi.searchurl.service

import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.searchurl.dto.SearchUrlResponse
import no.nav.navnosearchapi.searchurl.factory.UrlSearchQueryFactory
import org.springframework.stereotype.Service

@Service
class UrlSearchService(
    private val searchClient: SearchClient,
) {
    fun search(term: String): SearchUrlResponse {
        val query = UrlSearchQueryFactory.createBuilder(term)
        val hit = searchClient.search(query).searchHits.firstOrNull()

        return SearchUrlResponse(
            hit?.content?.href,
            hit?.content?.title?.value
        )
    }
}