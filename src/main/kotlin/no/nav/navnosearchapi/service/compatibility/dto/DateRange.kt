package no.nav.navnosearchapi.service.compatibility.dto

data class DateRange(
    val docCount: Long,
    val checked: Boolean,
    val buckets: List<DateRangeBucket>,
)