package no.nav.navnosearchapi.search.dto

data class ContentAggregations(
    val audience: Map<String, Long>,
    val language: Map<String, Long>,
    val fylke: Map<String, Long>,
    val metatags: Map<String, Long>,
    val dateRangeAggregations: Map<String, Long>,
    val isFile: Long,
    val totalCount: Long,
)