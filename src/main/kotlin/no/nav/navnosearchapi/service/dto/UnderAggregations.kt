package no.nav.navnosearchapi.service.dto

data class UnderAggregations(
    val buckets: List<FacetBucket> = emptyList(),
)