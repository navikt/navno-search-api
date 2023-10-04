package no.nav.navnosearchapi.search.compatibility.dto

data class SearchResult(
    val c: Int,
    val s: Int,
    val daterange: Int,
    val isMore: Boolean,
    val word: String,
    val total: Long,
    val fasettKey: String,
    val autoComplete: List<String>?,
    val aggregations: Aggregations,
    val hits: List<SearchHit>
)