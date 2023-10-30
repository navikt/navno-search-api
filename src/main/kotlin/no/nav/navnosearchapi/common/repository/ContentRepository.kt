package no.nav.navnosearchapi.common.repository

import no.nav.navnosearchapi.common.model.ContentDao
import org.springframework.data.repository.CrudRepository

interface ContentRepository : CrudRepository<ContentDao, String>