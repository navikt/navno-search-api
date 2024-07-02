package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.service.dto.SearchUrlResponse
import no.nav.navnosearchapi.service.utils.value
import org.springframework.stereotype.Service

@Service
class UrlSearchService(
    val searchClient: SearchClient,
) {
    fun search(term: String): SearchUrlResponse {
        return searchClient.searchUrl(term).searchHits.firstOrNull()?.content.let {
            SearchUrlResponse(
                it?.href,
                it?.title?.value()
            )
        }
    }
}