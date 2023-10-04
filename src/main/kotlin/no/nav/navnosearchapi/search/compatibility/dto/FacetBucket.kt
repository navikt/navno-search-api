package no.nav.navnosearchapi.search.compatibility.dto

data class FacetBucket(
    override val key: String,
    override val docCount: Long,
    override val checked: Boolean,
    val name: String,
    val underaggregeringer: UnderAggregations? = UnderAggregations()
): Bucket