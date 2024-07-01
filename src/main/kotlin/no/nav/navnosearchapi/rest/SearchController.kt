package no.nav.navnosearchapi.rest

import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.client.dto.SearchUrlResponse
import no.nav.navnosearchapi.service.Params
import no.nav.navnosearchapi.service.SearchService
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.dto.SearchResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    val searchClient: SearchClient,
    val searchService: SearchService,
    @Value("\${opensearch.page-size}") val pageSize: Int,
) {
    @GetMapping("/content/search")
    fun search(
        @ModelAttribute params: Params
    ): SearchResult {
        val result = searchClient.search(
            term = params.ord,
            pageSize = pageSize,
            page = params.page,
            filters = searchService.postAggregationFilters(params.f, params.uf),
            preAggregationFilters = searchService.preAggregationFilters(params.preferredLanguage),
            aggregations = searchService.aggregations(params.f, params.uf),
            sort = Sort.by(Sort.Direction.DESC, SORT_BY_DATE).takeIf { params.s == 1 }
        )

        return searchService.toSearchResult(params, result)
    }

    @GetMapping("/content/decorator-search")
    fun decoratorSearch(
        @ModelAttribute params: Params
    ): DecoratorSearchResult {
        val result = searchClient.search(
            term = params.ord,
            pageSize = DECORATOR_SEARCH_PAGE_SIZE,
            page = FIRST_PAGE,
            filters = searchService.decoratorSearchFilters(params.f, params.preferredLanguage),
        )

        return searchService.toDecoratorSearchResult(params, result)
    }

    @GetMapping("/content/search-url")
    @CrossOrigin
    fun searchUrl(
        @RequestParam term: String,
    ): SearchUrlResponse {
        return searchClient.searchUrl(term)
    }

    companion object {
        private const val DECORATOR_SEARCH_PAGE_SIZE = 5
        private const val FIRST_PAGE = 0
    }
}

