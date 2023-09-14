package no.nav.navnosearchapi.search

import no.nav.navnosearchapi.search.compatibility.CompatibilityService
import no.nav.navnosearchapi.search.compatibility.Params
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.service.SearchService
import no.nav.navnosearchapi.search.service.search.Filters
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(val searchService: SearchService, val compatibilityService: CompatibilityService) {

    @GetMapping("/content/search")
    fun search(
        @RequestParam term: String,
        @RequestParam page: Int,
        @ModelAttribute filters: Filters
    ): ContentSearchPage {
        return searchService.search(term, page, filters)
    }

    @GetMapping("/content/compatible-search")
    fun searchBackwardsCompatible(
        @ModelAttribute params: Params
    ): SearchResult {
        val result = searchService.search(
            term = params.ord,
            page = params.start,
            filters = compatibilityService.toFilters(params.f, params.uf, params.daterange)
        )

        return compatibilityService.toSearchResult(params, result)
    }
}

