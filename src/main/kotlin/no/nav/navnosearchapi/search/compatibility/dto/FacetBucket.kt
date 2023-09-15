package no.nav.navnosearchapi.search.compatibility.dto

data class FacetBucket(
    val key: String,
    val name: String,
    val docCount: Long,
    val checked: Boolean,
    val underaggregeringer: UnderAggregations? = UnderAggregations()
)