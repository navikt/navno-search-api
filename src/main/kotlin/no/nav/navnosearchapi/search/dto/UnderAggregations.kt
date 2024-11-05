package no.nav.navnosearchapi.search.dto

data class UnderAggregations(
    val buckets: List<FacetBucket> = emptyList(),
)