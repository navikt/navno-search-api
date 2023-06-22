package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: ContentRepository) {
    fun searchAllText(term: String): SearchHits<Content> = repository.searchAllText(term)
}