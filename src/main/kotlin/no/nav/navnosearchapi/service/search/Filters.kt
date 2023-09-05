package no.nav.navnosearchapi.service.search

import java.time.LocalDateTime

data class Filters(
    val audience: List<String>?,
    val language: List<String>?,
    val fylke: List<String>?,
    val metatags: List<String>?,
    val isFile: List<String>?,
    val lastUpdatedFrom: LocalDateTime?,
    val lastUpdatedTo: LocalDateTime?
)