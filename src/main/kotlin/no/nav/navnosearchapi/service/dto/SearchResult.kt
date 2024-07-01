package no.nav.navnosearchapi.service.dto

data class SearchResult(
    val page: Int,
    val s: Int,
    val preferredLanguage: String?,
    val isMore: Boolean,
    val word: String,
    val total: Long,
    val fasettKey: String,
    val autoComplete: List<String>?,
    val aggregations: Aggregations,
    val hits: List<SearchHit>
)