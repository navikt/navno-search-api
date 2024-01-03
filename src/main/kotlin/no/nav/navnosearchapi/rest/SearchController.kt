package no.nav.navnosearchapi.rest

import no.nav.navnosearchadminapi.common.constants.LAST_UPDATED
import no.nav.navnosearchapi.service.compatibility.CompatibilityService
import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.search.SearchService
import no.nav.navnosearchapi.service.search.dto.SearchUrlResponse
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    val searchService: SearchService,
    val compatibilityService: CompatibilityService
) {
    @GetMapping("/content/search")
    fun search(
        @ModelAttribute params: Params
    ): SearchResult {
        val result = searchService.search(
            term = compatibilityService.term(params.ord),
            page = params.start,
            preAggregationFilters = compatibilityService.preAggregationFilters(params.audience, params.preferredLanguage),
            postAggregationFilters = compatibilityService.postAggregationFilters(params.f, params.uf, params.daterange),
            aggregations = compatibilityService.aggregations(params.f, params.uf),
            mapCustomAggregations = true,
            sort = if (params.s == 1) Sort.by(Sort.Direction.DESC, LAST_UPDATED) else null
        )

        return compatibilityService.toSearchResult(params, result)
    }

    @GetMapping("/content/search-url")
    fun searchUrl(
        @RequestParam term: String,
    ): SearchUrlResponse {
        return searchService.searchUrl(term)
    }
}

