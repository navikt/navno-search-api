package no.nav.navnosearchapi.admin.service

import no.nav.navnosearchapi.admin.mapper.ContentMapper
import no.nav.navnosearchapi.admin.repository.ContentRepository
import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.exception.DocumentForTeamNameNotFoundException
import no.nav.navnosearchapi.common.mapper.ContentDtoMapper
import no.nav.navnosearchapi.common.utils.createInternalId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class AdminService(
    val contentDtoMapper: ContentDtoMapper,
    val contentMapper: ContentMapper,
    val repository: ContentRepository,
    @Value("\${opensearch.page-size}") val pageSize: Int,
) {
    fun saveAllContent(content: List<ContentDto>, teamName: String) {
        val mappedContent = content.map { contentMapper.toContentDao(it, teamName) }
        repository.saveAll(mappedContent)
    }

    fun deleteContentByTeamNameAndId(teamName: String, externalId: String) {
        val id = createInternalId(teamName, externalId)
        if (repository.existsById(id)) {
            repository.deleteById(id)
        } else {
            throw DocumentForTeamNameNotFoundException("Dokument med ekstern id $externalId finnes ikke for team $teamName")
        }
    }

    fun getContentForTeamName(teamName: String, page: Int): Page<ContentDto> {
        val pageable = PageRequest.of(page, pageSize)
        return repository.findAllByTeamOwnedBy(teamName, pageable).map { contentDtoMapper.toContentDto(it) }
    }
}