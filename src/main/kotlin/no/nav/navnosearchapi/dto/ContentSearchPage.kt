package no.nav.navnosearchapi.dto

data class ContentSearchPage(
    val hits: List<ContentSearchHit>,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int
)