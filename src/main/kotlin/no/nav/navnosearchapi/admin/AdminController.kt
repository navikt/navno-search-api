package no.nav.navnosearchapi.admin

import no.nav.navnosearchapi.admin.service.AdminService
import no.nav.navnosearchapi.admin.validation.ContentDtoValidator
import no.nav.navnosearchapi.common.dto.ContentDto
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(val service: AdminService, val validator: ContentDtoValidator) {

    @PostMapping("/content/{teamName}")
    fun saveContent(
        @RequestBody content: List<ContentDto>,
        @PathVariable teamName: String
    ): String {
        validator.validate(content)
        service.saveAllContent(content, teamName)
        return "${content.size} dokumenter indeksert"
    }

    @GetMapping("/content/{teamName}")
    fun getContentForTeamName(
        @PathVariable teamName: String,
        @RequestParam page: Int
    ): Page<ContentDto> {
        return service.getContentForTeamName(teamName, page)
    }

    @DeleteMapping("/content/{teamName}/{id}")
    fun deleteContentByTeamNameAndId(@PathVariable teamName: String, @PathVariable id: String): String {
        service.deleteContentByTeamNameAndId(teamName, id)
        return "Dokument med id $id slettet"
    }
}