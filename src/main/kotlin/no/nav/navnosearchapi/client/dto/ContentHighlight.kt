package no.nav.navnosearchapi.client.dto

data class ContentHighlight(
    val title: List<String> = emptyList(),
    val ingress: List<String> = emptyList(),
    val text: List<String> = emptyList(),
)