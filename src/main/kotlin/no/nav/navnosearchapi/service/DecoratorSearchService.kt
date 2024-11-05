package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.mapper.DecoratorSearchResultMapper
import no.nav.navnosearchapi.service.utils.isInQuotes
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class DecoratorSearchService(
    val decoratorSearchResultMapper: DecoratorSearchResultMapper,
    val searchClient: SearchClient,
) {
    fun search(params: Params): DecoratorSearchResult {
        val searchPage = searchClient.search(
            params = params,
            pageRequest = PageRequest.of(FIRST_PAGE, DECORATOR_SEARCH_PAGE_SIZE)
        )
        return decoratorSearchResultMapper.toSearchResult(
            params = params,
            result = searchPage,
            isMatchPhraseQuery = params.ord.isInQuotes()
        )
    }

    companion object {
        private const val DECORATOR_SEARCH_PAGE_SIZE = 5
        private const val FIRST_PAGE = 0
    }
}