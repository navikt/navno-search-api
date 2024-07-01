package no.nav.navnosearchapi.rest

import no.nav.navnosearchapi.service.CompleteSearchService
import no.nav.navnosearchapi.service.DecoratorSearchService
import no.nav.navnosearchapi.service.UrlSearchService
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.dto.SearchUrlResponse
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    val completeSearchService: CompleteSearchService,
    val decoratorSearchService: DecoratorSearchService,
    val urlSearchService: UrlSearchService,
) {
    @GetMapping("/content/search")
    fun search(@ModelAttribute params: Params): SearchResult {
        return completeSearchService.search(params)
    }

    @GetMapping("/content/decorator-search")
    fun decoratorSearch(@ModelAttribute params: Params): DecoratorSearchResult {
        return decoratorSearchService.search(params)
    }

    @GetMapping("/content/search-url")
    @CrossOrigin
    fun searchUrl(@RequestParam term: String): SearchUrlResponse {
        return urlSearchService.search(term)
    }
}

