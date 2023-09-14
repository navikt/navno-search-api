package no.nav.navnosearchapi.admin.repository

import no.nav.navnosearchapi.common.model.ContentDao
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface ContentRepository : CrudRepository<ContentDao, String> {
    fun findAllByTeamOwnedBy(teamOwnedBy: String, pageable: Pageable): Page<ContentDao>
}