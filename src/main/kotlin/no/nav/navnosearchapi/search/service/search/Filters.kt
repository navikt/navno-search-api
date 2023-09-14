package no.nav.navnosearchapi.search.service.search

import java.time.LocalDateTime

data class Filters(
    val audience: List<String>? = null,
    val language: List<String>? = null,
    val fylke: List<String>? = null,
    val metatags: List<String>? = null,
    val isFile: List<String>? = null,
    val lastUpdatedFrom: LocalDateTime? = null,
    val lastUpdatedTo: LocalDateTime? = null,
)