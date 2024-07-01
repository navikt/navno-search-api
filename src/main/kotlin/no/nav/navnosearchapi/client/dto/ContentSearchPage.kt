package no.nav.navnosearchapi.client.dto

data class ContentSearchPage(
    val suggestions: List<String>?,
    val hits: List<ContentSearchHit>,
    val aggregations: Map<String, Long>?,
    val totalPages: Int,
    val totalElements: Long,
    val pageSize: Int,
    val pageNumber: Int
)