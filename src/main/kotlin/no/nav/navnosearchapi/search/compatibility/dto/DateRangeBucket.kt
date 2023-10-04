package no.nav.navnosearchapi.search.compatibility.dto

data class DateRangeBucket(
    override val key: String,
    override val docCount: Long,
    override val checked: Boolean,
): Bucket