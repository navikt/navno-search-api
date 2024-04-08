package no.nav.navnosearchapi.rest

import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchapi.service.compatibility.CompatibilityService
import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.search.SearchService
import no.nav.navnosearchapi.service.search.dto.SearchUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    val searchService: SearchService,
    val compatibilityService: CompatibilityService,
    @Value("\${opensearch.page-size}") val pageSize: Int,
) {
    @GetMapping("/content/search")
    fun search(
        @ModelAttribute params: Params
    ): SearchResult {
        val result = searchService.search(
            term = params.ord,
            pageSize = pageSize,
            page = params.page,
            filters = compatibilityService.postAggregationFilters(params.f, params.uf),
            preAggregationFilters = compatibilityService.preAggregationFilters(params.preferredLanguage),
            aggregations = compatibilityService.aggregations(params.f, params.uf),
            sort = if (params.s == 1) Sort.by(Sort.Direction.DESC, SORT_BY_DATE) else null
        )

        return compatibilityService.toSearchResult(params, result)
    }

    @GetMapping("/content/decorator-search")
    fun decoratorSearch(
        @ModelAttribute params: Params
    ): DecoratorSearchResult {
        val result = searchService.search(
            term = params.ord,
            pageSize = DECORATOR_SEARCH_PAGE_SIZE,
            page = FIRST_PAGE,
            filters = compatibilityService.decoratorSearchFilters(params.f, params.preferredLanguage),
        )

        return compatibilityService.toDecoratorSearchResult(params, result)
    }

    @GetMapping("/content/search-url")
    @CrossOrigin
    fun searchUrl(
        @RequestParam term: String,
    ): SearchUrlResponse {
        return searchService.searchUrl(term)
    }

    companion object {
        private const val DECORATOR_SEARCH_PAGE_SIZE = 5
        private const val FIRST_PAGE = 0
    }
}

