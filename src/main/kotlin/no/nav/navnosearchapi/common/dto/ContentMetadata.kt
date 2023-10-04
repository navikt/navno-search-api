package no.nav.navnosearchapi.common.dto

import java.time.LocalDateTime

data class ContentMetadata(
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
    val audience: List<String>,
    val language: String,
    val isFile: Boolean = false,
    val fylke: String? = null,
    val metatags: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
)