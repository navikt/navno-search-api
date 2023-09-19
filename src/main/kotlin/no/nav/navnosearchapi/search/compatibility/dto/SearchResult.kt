package no.nav.navnosearchapi.search.compatibility.dto

data class SearchResult(
    val c: Int,
    val s: Int,
    val daterange: Int,
    val isMore: Boolean,
    val word: String,
    val total: Long,
    val fasettKey: String,
    val aggregations: Aggregations,
    val hits: List<SearchHit>,
    val isInitialResult: Boolean,
    val autoComplete: String?
)