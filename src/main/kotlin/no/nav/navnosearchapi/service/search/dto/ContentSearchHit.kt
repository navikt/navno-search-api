package no.nav.navnosearchapi.service.search.dto

import java.time.ZonedDateTime

data class ContentSearchHit(
    val title: String,
    val ingress: String,
    val text: String,
    val href: String,
    val audience: List<String>,
    val language: String,
    val lastUpdated: ZonedDateTime,
    val highlight: ContentHighlight,
    val isKontor: Boolean,
    val score: Float,
)