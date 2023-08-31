package no.nav.navnosearchapi.dto

data class ContentDto(
    val id: String,
    val href: String,
    val title: String,
    val ingress: String,
    val text: String,
    val metadata: ContentMetadata
)