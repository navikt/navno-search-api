package no.nav.navnosearchapi.rest

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.AdminService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import no.nav.navnosearchapi.dto.Content as ContentDto

@RestController
class AdminController(val service: AdminService) {

    @PostMapping("/content/{appName}")
    fun saveContent(@RequestBody content: List<ContentDto>, @PathVariable("appName") appName: String): List<Content> {
        return service.saveAllContent(content, appName)
    }

    @GetMapping("/content/{appName}")
    fun getContentForAppName(
        @PathVariable appName: String,
        @RequestParam page: Int
    ): ContentSearchPage {
        return service.getContentForAppName(appName, page)
    }

    @DeleteMapping("/content/{appName}/{id}")
    fun deleteContentByAppNameAndId(@PathVariable appName: String, @PathVariable id: String): String {
        return service.deleteContentByAppNameAndId(appName, id)
    }
}