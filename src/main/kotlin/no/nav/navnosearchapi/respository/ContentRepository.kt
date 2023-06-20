package no.nav.navnosearchapi.respository

import no.nav.navnosearchapi.model.Content
import org.springframework.data.repository.CrudRepository

interface ContentRepository : CrudRepository<Content, Long> {
    fun findByIngressLike(word: String): List<Content>
}

