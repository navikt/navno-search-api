package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.mapper.DecoratorSearchResultMapper
import no.nav.navnosearchapi.service.utils.activeFasettFilterQuery
import no.nav.navnosearchapi.service.utils.activePreferredLanguageFilterQuery
import no.nav.navnosearchapi.service.utils.isInQuotes
import org.opensearch.index.query.BoolQueryBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class DecoratorSearchService(
    val decoratorSearchResultMapper: DecoratorSearchResultMapper,
    val searchClient: SearchClient,
) {
    fun search(params: Params): DecoratorSearchResult {
        val isMatchPhraseQuery = isInQuotes(params.ord)
        val searchPage = searchClient.search(
            term = params.ord,
            isMatchPhraseQuery = isMatchPhraseQuery,
            filters = decoratorSearchFilters(params.f, params.preferredLanguage),
            pageRequest = PageRequest.of(FIRST_PAGE, DECORATOR_SEARCH_PAGE_SIZE)
        )
        return decoratorSearchResultMapper.toSearchResult(params, searchPage, isMatchPhraseQuery)
    }

    fun decoratorSearchFilters(facet: String, preferredLanguage: String?) = BoolQueryBuilder().apply {
        this.must(activeFasettFilterQuery(facet, emptyList()))
        if (preferredLanguage != null) {
            this.must(activePreferredLanguageFilterQuery(preferredLanguage))
        }
    }


    companion object {
        private const val DECORATOR_SEARCH_PAGE_SIZE = 5
        private const val FIRST_PAGE = 0
    }
}