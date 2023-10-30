package no.nav.navnosearchapi.service.compatibility.dto

data class SearchHit(
    val displayName: String,
    val href: String,
    val highlight: String,
    val modifiedTime: String,
    val audience: List<String>,
    val language: String,
)