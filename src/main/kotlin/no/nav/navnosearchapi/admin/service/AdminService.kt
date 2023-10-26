package no.nav.navnosearchapi.admin.service

import no.nav.navnosearchapi.admin.dto.inbound.ContentDto
import no.nav.navnosearchapi.admin.dto.outbound.SaveContentResponse
import no.nav.navnosearchapi.admin.mapper.ContentDtoMapper
import no.nav.navnosearchapi.admin.mapper.ContentMapper
import no.nav.navnosearchapi.admin.repository.ContentRepository
import no.nav.navnosearchapi.admin.validation.ContentDtoValidator
import no.nav.navnosearchapi.common.exception.DocumentForTeamNameNotFoundException
import no.nav.navnosearchapi.common.utils.createInternalId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class AdminService(
    val validator: ContentDtoValidator,
    val contentDtoMapper: ContentDtoMapper,
    val contentMapper: ContentMapper,
    val repository: ContentRepository,
    @Value("\${opensearch.page-size}") val pageSize: Int,
) {
    val logger: Logger = LoggerFactory.getLogger(AdminService::class.java)

    fun saveAllContent(content: List<ContentDto>, teamName: String): SaveContentResponse {
        val validationErrors = validator.validate(content)

        if (validationErrors.isNotEmpty()) {
            logger.info("Fikk valideringsfeil ved indeksering av innhold: $validationErrors")
        }

        val mappedContent = content
            .filter { !validationErrors.containsKey(it.id) }
            .map { contentMapper.toContentDao(it, teamName) }
        repository.saveAll(mappedContent)

        val numberOfIndexedDocuments = mappedContent.size
        val numberOfFailedDocuments = validationErrors.size
        logger.info("$numberOfIndexedDocuments dokumenter indeksert, $numberOfFailedDocuments dokumenter feilet")

        return SaveContentResponse(numberOfIndexedDocuments, numberOfFailedDocuments, validationErrors)
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