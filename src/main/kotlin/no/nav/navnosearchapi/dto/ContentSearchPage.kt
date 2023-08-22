package no.nav.navnosearchapi.dto

data class ContentSearchPage(
    val suggestions: List<String?>,
    val hits: List<ContentSearchHit>,
    val totalPages: Int,
    val totalElements: Long,
    val pageSize: Int,
    val pageNumber: Int
)