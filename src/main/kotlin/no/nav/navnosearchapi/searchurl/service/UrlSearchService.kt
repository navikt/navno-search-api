package no.nav.navnosearchapi.searchurl.service

import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.searchurl.dto.SearchUrlResponse
import no.nav.navnosearchapi.searchurl.factory.UrlSearchQueryFactory
import org.springframework.stereotype.Service

@Service
class UrlSearchService(
    val searchQueryService: UrlSearchQueryFactory,
    val searchClient: SearchClient,
) {
    fun search(term: String): SearchUrlResponse {
        val query = searchQueryService.createBuilder(term)
        val result = searchClient.search(query)
        return result.searchHits.firstOrNull()?.content.let {
            SearchUrlResponse(
                it?.href,
                it?.title?.value
            )
        }
    }
}