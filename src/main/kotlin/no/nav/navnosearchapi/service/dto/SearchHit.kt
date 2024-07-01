package no.nav.navnosearchapi.service.dto

import java.time.ZonedDateTime

data class SearchHit(
    val displayName: String,
    val href: String,
    val highlight: String,
    val modifiedTime: ZonedDateTime?,
    val publishedTime: ZonedDateTime?,
    val audience: List<String>,
    val language: String,
    val type: String,
    val score: Float,
)