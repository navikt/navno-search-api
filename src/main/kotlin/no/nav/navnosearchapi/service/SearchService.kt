package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.ContentRepository
import org.springframework.stereotype.Service

@Service
class SearchService(val repository: ContentRepository) {
    fun findByIngressLike(word: String): List<Content> = repository.findByIngressLike(word).toList()
}