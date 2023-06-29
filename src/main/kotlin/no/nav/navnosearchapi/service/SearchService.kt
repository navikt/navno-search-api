package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import no.nav.navnosearchapi.utils.indexName
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: ContentRepository, @Value("\${opensearch.page-size}") val pageSize: Int) {
    fun searchAllText(term: String, page: Int): Page<Content> {
        val pageRequest = PageRequest.of(page, pageSize)

        if (isInQuotes(term)) {
            return repository.searchAllTextForPhrase(term, pageRequest)
        }

        return repository.searchAllText(term, pageRequest)
    }

    fun getContentForApp(appName: String, page: Int): Page<Content> {
        val pageRequest = PageRequest.of(page, pageSize)

        return repository.findAllByIndex(indexName(appName), pageRequest)
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith('"') && term.endsWith('"')
    }
}