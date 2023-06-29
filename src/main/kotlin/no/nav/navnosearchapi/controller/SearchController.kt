package no.nav.navnosearchapi.controller

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.SearchService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(val service: SearchService) {

    @GetMapping("/content/search")
    fun searchAllText(@RequestParam term: String, @RequestParam page: Int): Page<Content> {
        return service.searchAllText(term, page)
    }

    @GetMapping("/content/{appName}")
    fun getContentForApp(@PathVariable appName: String, @RequestParam page: Int): Page<Content> {
        return service.getContentForApp(appName, page)
    }
}