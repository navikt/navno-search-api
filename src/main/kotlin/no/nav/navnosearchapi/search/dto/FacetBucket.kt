package no.nav.navnosearchapi.search.dto

data class FacetBucket(
    val key: String,
    val docCount: Long,
    val checked: Boolean,
    val name: String,
    val underaggregeringer: UnderAggregations? = null
)