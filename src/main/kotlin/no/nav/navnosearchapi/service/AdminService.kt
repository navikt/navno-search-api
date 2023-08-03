package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.exception.NoIndexForAppException
import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.service.search.SearchHelper
import no.nav.navnosearchapi.service.search.findAllByIndexQuery
import no.nav.navnosearchapi.utils.indexCoordinates
import no.nav.navnosearchapi.utils.indexName
import org.springframework.data.elasticsearch.NoSuchIndexException
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service

@Service
class AdminService(
    val operations: ElasticsearchOperations,
    val searchHelper: SearchHelper,
) {

    fun saveAllContent(content: List<Content>, appName: String): List<Content> {
        return operations.save(content, indexCoordinates(appName)).toList()
    }

    fun deleteContentByAppNameAndId(appName: String, id: String): String {
        try {
            return operations.delete(id, indexCoordinates(appName))
        } catch (ex: NoSuchIndexException) {
            throw NoIndexForAppException(appName, ex)
        }
    }

    fun getContentForAppName(appName: String, page: Int): ContentSearchPage {
        return searchHelper.search(findAllByIndexQuery(indexName(appName)), page, false)
    }
}