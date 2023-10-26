package no.nav.navnosearchapi.admin.dto.inbound

import java.time.ZonedDateTime

data class ContentMetadata(
    val createdAt: ZonedDateTime? = null,
    val lastUpdated: ZonedDateTime? = null,
    val audience: List<String>? = null,
    val language: String? = null,
    val isFile: Boolean = false,
    val fylke: String? = null,
    val metatags: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
)