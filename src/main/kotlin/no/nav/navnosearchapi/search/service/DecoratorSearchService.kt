package no.nav.navnosearchapi.search.service

import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.DecoratorSearchResult
import no.nav.navnosearchapi.search.factory.SearchQueryFactory
import no.nav.navnosearchapi.search.mapper.toDecoratorSearchResult
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class DecoratorSearchService(
    val searchQueryFactory: SearchQueryFactory,
    val searchClient: SearchClient,
) {
    fun search(params: Params): DecoratorSearchResult {
        val query = searchQueryFactory.createBuilder(params)
        val result = searchClient.searchForPage(query, PageRequest.of(FIRST_PAGE, DECORATOR_SEARCH_PAGE_SIZE))
        return result.toDecoratorSearchResult(params)
    }

    companion object {
        private const val DECORATOR_SEARCH_PAGE_SIZE = 5
        private const val FIRST_PAGE = 0
    }
}