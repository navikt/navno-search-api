package no.nav.navnosearchapi.service.dto

data class DecoratorSearchHit(
    val displayName: String,
    val href: String,
    val highlight: String,
)