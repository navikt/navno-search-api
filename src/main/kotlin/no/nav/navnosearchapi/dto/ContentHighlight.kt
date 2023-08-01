package no.nav.navnosearchapi.dto

data class ContentHighlight(
    val name: List<String> = emptyList(),
    val ingress: List<String> = emptyList(),
    val text: List<String> = emptyList()
)