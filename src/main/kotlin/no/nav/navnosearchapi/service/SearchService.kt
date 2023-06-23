package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: ContentRepository) {
    fun searchAllText(term: String, page: Int): Page<Content> {
        val pageRequest = PageRequest.of(page, 10)

        if (term.startsWith('"') && term.endsWith('"')) {
            return repository.searchAllTextForPhrase(term, pageRequest)
        }

        return repository.searchAllText(term, pageRequest)
    }
}