package no.nav.navnosearchapi.common.dto

import java.time.ZonedDateTime

data class ContentMetadata(
    val createdAt: ZonedDateTime,
    val lastUpdated: ZonedDateTime,
    val audience: List<String>,
    val language: String,
    val isFile: Boolean = false,
    val fylke: String? = null,
    val metatags: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
)