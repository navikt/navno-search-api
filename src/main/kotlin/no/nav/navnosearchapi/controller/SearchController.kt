package no.nav.navnosearchapi.controller

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.SearchService
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(val service: SearchService) {

    @GetMapping("/content/search")
    fun getContent(@RequestParam term: String): SearchHits<Content> {
        return service.searchAllText(term)
    }
}