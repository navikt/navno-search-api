package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import no.nav.navnosearchapi.utils.indexCoordinates
import no.nav.navnosearchapi.utils.indexName
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service

@Service
class AdminService(
    val repository: ContentRepository,
    val operations: ElasticsearchOperations,
    @Value("\${opensearch.page-size}") val pageSize: Int
) {
    fun saveContent(content: Content, appName: String): Content {
        return operations.save(content, indexCoordinates(appName))
    }

    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexCoordinates(appName)).toList()
    }

    fun deleteContentByAppNameAndId(appName: String, id: String): String {
        return operations.delete(id, indexCoordinates(appName))
    }

    fun getContentForAppName(appName: String, page: Int): Page<Content> {
        val pageRequest = PageRequest.of(page, pageSize)

        return repository.findAllByIndex(indexName(appName), pageRequest)
    }
}