package no.nav.navnosearchapi.dto

data class ContentDto(
    val id: String,
    val href: String,
    val name: String,
    val ingress: String,
    val text: String,
    val audience: String,
    val language: String,
)