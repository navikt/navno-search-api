package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Service

@Service
class IndexService(private val operations: ElasticsearchOperations) {
    fun saveContent(content: Content, appName: String): Content {
        return operations.save(content, indexName(appName))
    }

    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexName(appName)).toList()
    }

    private fun indexName(appName: String): IndexCoordinates {
        return IndexCoordinates.of(INDEX_PREFIX + appName)
    }

    companion object {
        const val INDEX_PREFIX = "search_content_"
    }
}