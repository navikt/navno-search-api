package no.nav.navnosearchapi.search.compatibility.dto

data class UnderAggregations(
    val buckets: List<FacetBucket>? = emptyList(),
)