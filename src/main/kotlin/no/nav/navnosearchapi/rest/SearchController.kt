package no.nav.navnosearchapi.rest

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.SearchService
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(val service: SearchService) {

    @GetMapping("/content/search")
    fun searchAllText(
        @RequestParam term: String,
        @RequestParam(required = false) maalgruppe: List<String>?,
        @RequestParam page: Int
    ): SearchPage<Content> {
        return service.searchAllText(term, maalgruppe, page)
    }
}