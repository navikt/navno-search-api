package no.nav.navnosearchapi.dto

import java.time.LocalDateTime

data class ContentMetadata(
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
    val audience: List<String>,
    val language: String,
    val isFile: Boolean? = null,
    val fylke: String? = null,
    val metatags: List<String>? = null,
)