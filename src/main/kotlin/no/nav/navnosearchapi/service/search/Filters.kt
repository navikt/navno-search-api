package no.nav.navnosearchapi.service.search

import java.time.ZonedDateTime

data class Filters(
    val audience: List<String>?,
    val language: List<String>?,
    val fylke: List<String>?,
    val metatags: List<String>?,
    val isFile: ZonedDateTime?,
    val lastEditedFrom: ZonedDateTime?,
    val lastEditedTo: ZonedDateTime?
)