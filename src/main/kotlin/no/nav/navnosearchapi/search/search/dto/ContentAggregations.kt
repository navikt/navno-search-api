package no.nav.navnosearchapi.search.search.dto

data class ContentAggregations(
    val audience: Map<String, Long>? = null,
    val language: Map<String, Long>? = null,
    val fylke: Map<String, Long>? = null,
    val metatags: Map<String, Long>? = null,
    val dateRangeAggregations: Map<String, Long>? = null,
    val isFile: Long? = null,
    val totalCount: Long? = null,
    val custom: Map<String, Long>? = null
)