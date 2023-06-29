package no.nav.navnosearchapi.controller

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.AdminService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(val service: AdminService) {

    @PostMapping("/content/save")
    fun saveContent(@RequestBody content: Content, @RequestHeader("app-name") appName: String): Content {
        return service.saveContent(content, appName)
    }

    @PostMapping("/content/saveAll")
    fun saveAllContent(@RequestBody content: List<Content>, @RequestHeader("app-name") appName: String): List<Content> {
        return service.saveAllContent(content, appName)
    }

    @GetMapping("/content/{appName}")
    fun getContentForAppName(@PathVariable appName: String, @RequestParam page: Int): Page<Content> {
        return service.getContentForAppName(appName, page)
    }

    @DeleteMapping("/content/{appName}/{id}")
    fun deleteContentByAppNameAndId(@PathVariable appName: String, @PathVariable id: String): String {
        return service.deleteContentByAppNameAndId(appName, id)
    }
}