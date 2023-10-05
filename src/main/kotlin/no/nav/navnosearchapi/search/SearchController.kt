package no.nav.navnosearchapi.search

import no.nav.navnosearchapi.common.utils.LAST_UPDATED
import no.nav.navnosearchapi.search.compatibility.CompatibilityService
import no.nav.navnosearchapi.search.compatibility.Params
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.service.SearchService
import no.nav.navnosearchapi.search.service.search.Filter
import org.springframework.data.domain.Sort
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
        @ModelAttribute filter: Filter
    ): ContentSearchPage {
        return searchService.search(term, page, listOf(filter.toQuery()))
    }

    @GetMapping("/content/compatible-search")
    fun searchBackwardsCompatible(
        @ModelAttribute params: Params
    ): SearchResult {
        val filters = compatibilityService.toFilters(params.f, params.uf, params.daterange)
        val aggregations = compatibilityService.aggregations(filters)
        val term = compatibilityService.term(params.ord)

        val result = searchService.search(
            term = term,
            page = params.start,
            filters = filters,
            aggregations = aggregations,
            mapCustomAggregations = true,
            sort = if (params.s == 1) Sort.by(Sort.Direction.DESC, LAST_UPDATED) else null
        )

        return compatibilityService.toSearchResult(params, result)
    }
}

