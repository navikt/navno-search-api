package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.utils.indexCoordinates
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service

@Service
class AdminService(val operations: ElasticsearchOperations) {

    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexCoordinates(appName)).toList()
    }

    fun deleteContentByAppNameAndId(appName: String, id: String): String {
        return operations.delete(id, indexCoordinates(appName))
    }

    fun getContentForAppName(appName: String): SearchHits<Content> {
        return operations.search(operations.matchAllQuery(), Content::class.java, indexCoordinates(appName))
    }
}