package no.nav.navnosearchapi.common.repository

import no.nav.navnosearchadminapi.common.model.Content
import org.springframework.data.repository.CrudRepository

interface ContentRepository : CrudRepository<Content, String> {
}