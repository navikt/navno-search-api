package no.nav.navnosearchapi.repository

import no.nav.navnosearchapi.model.ContentDao
import org.springframework.data.repository.CrudRepository

interface ContentRepository : CrudRepository<ContentDao, String>