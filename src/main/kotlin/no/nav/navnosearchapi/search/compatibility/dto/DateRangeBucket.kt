package no.nav.navnosearchapi.search.compatibility.dto

data class DateRangeBucket(
    val key: String,
    val docCount: Long,
    val checked: Boolean,
)