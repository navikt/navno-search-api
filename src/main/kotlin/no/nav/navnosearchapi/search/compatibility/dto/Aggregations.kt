package no.nav.navnosearchapi.search.compatibility.dto

data class Aggregations(
    val fasetter: UnderAggregations,
    val tidsperiode: DateRange
)