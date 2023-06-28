package no.nav.navnosearchapi.controller

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.IndexService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController(val service: IndexService) {

    @PostMapping("/content/save")
    fun saveContent(@RequestBody content: Content, @RequestHeader("app-name") appName: String): Content {
        return service.saveContent(content, appName)
    }

    @PostMapping("/content/saveAll")
    fun saveAllContent(@RequestBody content: List<Content>, @RequestHeader("app-name") appName: String): List<Content> {
        return service.saveAllContent(content, appName)
    }
}