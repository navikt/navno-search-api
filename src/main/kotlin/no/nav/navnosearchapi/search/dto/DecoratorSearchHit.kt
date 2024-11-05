package no.nav.navnosearchapi.search.dto

data class DecoratorSearchHit(
    val displayName: String,
    val href: String,
    val highlight: String,
)