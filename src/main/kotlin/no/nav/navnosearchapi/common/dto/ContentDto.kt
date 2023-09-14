package no.nav.navnosearchapi.common.dto

data class ContentDto(
    val id: String,
    val href: String,
    val title: String,
    val ingress: String,
    val text: String,
    val metadata: ContentMetadata
)