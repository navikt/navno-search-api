package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.search.SearchHelper
import no.nav.navnosearchapi.service.search.findAllByIndexQuery
import no.nav.navnosearchapi.utils.indexCoordinates
import no.nav.navnosearchapi.utils.indexName
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Service

@Service
class AdminService(
    val searchHelper: SearchHelper,
    val operations: ElasticsearchOperations,
) {
    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexCoordinates(appName)).toList()
    }

    fun deleteContentByAppNameAndId(appName: String, id: String): String {
        return operations.delete(id, indexCoordinates(appName))
    }

    fun getContentForAppName(appName: String, page: Int): SearchPage<Content> {
        val query = findAllByIndexQuery(indexName(appName))
        return searchHelper.search(query, page)
    }
}