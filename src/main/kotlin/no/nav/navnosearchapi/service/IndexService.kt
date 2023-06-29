package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.utils.indexCoordinates
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service

@Service
class IndexService(private val operations: ElasticsearchOperations) {
    fun saveContent(content: Content, appName: String): Content {
        return operations.save(content, indexCoordinates(appName))
    }

    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexCoordinates(appName)).toList()
    }
}