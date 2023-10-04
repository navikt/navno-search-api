package no.nav.navnosearchapi.search.compatibility.dto

data class SearchHit(
    val displayName: String,
    val href: String,
    val highlight: String,
    val modifiedTime: String,
    val audience: List<String>,
    val language: String,
)