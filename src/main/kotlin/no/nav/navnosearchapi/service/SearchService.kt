package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: ContentRepository, @Value("\${opensearch.page-size}") val pageSize: Int) {
    fun searchAllText(term: String, page: Int): Page<Content> {
        val pageRequest = PageRequest.of(page, pageSize)

        if (term.startsWith('"') && term.endsWith('"')) {
            return repository.searchAllTextForPhrase(term, pageRequest)
        }

        return repository.searchAllText(term, pageRequest)
    }
}