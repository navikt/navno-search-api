package no.nav.navnosearchapi.client.dto

import java.time.ZonedDateTime

data class ContentSearchHit(
    val title: String,
    val ingress: String,
    val text: String,
    val href: String,
    val audience: List<String>,
    val language: String,
    val modifiedTime: ZonedDateTime?,
    val publishedTime: ZonedDateTime?,
    val highlight: ContentHighlight,
    val type: String,
    val score: Float,
)