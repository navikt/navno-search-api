package no.nav.navnosearchapi.search

import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.service.SearchService
import no.nav.navnosearchapi.search.service.search.Filters
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(val service: SearchService) {

    @GetMapping("/content/search")
    fun searchAllText(
        @RequestParam term: String,
        @RequestParam page: Int,
        @ModelAttribute filters: Filters
    ): ContentSearchPage {
        return service.searchAllText(term, page, filters)
    }
}