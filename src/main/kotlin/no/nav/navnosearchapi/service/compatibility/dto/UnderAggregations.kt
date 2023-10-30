package no.nav.navnosearchapi.service.compatibility.dto

data class UnderAggregations(
    val buckets: List<FacetBucket>? = emptyList(),
)