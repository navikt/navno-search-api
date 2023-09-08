package no.nav.navnosearchapi.rest

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.service.SearchService
import no.nav.navnosearchapi.service.search.Filters
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