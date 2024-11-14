package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.SearchHit
import no.nav.navnosearchapi.search.dto.SearchResult
import no.nav.navnosearchapi.search.utils.isInQuotes
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.springframework.data.elasticsearch.core.AggregationsContainer
import org.springframework.data.elasticsearch.core.SearchPage
import java.time.ZonedDateTime

private val providerSubaudiences = listOf(
    ValidAudiences.PROVIDER_DOCTOR.descriptor,
    ValidAudiences.PROVIDER_MUNICIPALITY_EMPLOYED.descriptor,
    ValidAudiences.PROVIDER_OPTICIAN.descriptor,
    ValidAudiences.PROVIDER_ADMINISTRATOR.descriptor,
    ValidAudiences.PROVIDER_MEASURES_ORGANIZER.descriptor,
    ValidAudiences.PROVIDER_AID_SUPPLIER.descriptor,
    ValidAudiences.PROVIDER_OTHER.descriptor,
)

private val metatagsWithModifiedTime = setOf(
    ValidMetatags.PRESSEMELDING.descriptor,
    ValidMetatags.PRESSE.descriptor,
    ValidMetatags.ANALYSE.descriptor,
    ValidMetatags.STATISTIKK.descriptor,
)

fun toSearchResult(params: Params, searchPage: SearchPage<Content>): SearchResult {
    return SearchResult(
        page = params.page,
        s = params.s,
        preferredLanguage = params.preferredLanguage,
        isMore = !searchPage.isLast,
        word = params.ord,
        total = searchPage.totalElements,
        fasettKey = params.f,
        aggregations = searchPage.searchHits.aggregations?.asMap()?.toAggregations(params),
        hits = searchPage.searchHits.searchHits.map { searchHit ->
            searchHit.content.toHit(
                searchHit.toHighlight(params.ord.isInQuotes()),
                searchHit.score
            )
        },
    )
}

private fun <T> AggregationsContainer<T>.asMap(): Map<String, Long> {
    return (this as OpenSearchAggregations).aggregations().associate { it.name to (it as Filter).docCount }
}

private fun Content.toHit(highlight: String, score: Float): SearchHit {
    val (publishedTime, modifiedTime) = resolveTimestamps(createdAt, lastUpdated, metatags, fylke)
    return SearchHit(
        displayName = title.value,
        href = href,
        highlight = highlight,
        modifiedTime = modifiedTime,
        publishedTime = publishedTime,
        audience = toAudience(audience),
        language = language,
        type = type,
        score = score,
    )
}

private fun toAudience(audience: List<String>): List<String> {
    // Filtrer ut overordnet Provider-audience hvis den inneholder mer presist audience
    return if (audience.any { it in providerSubaudiences }) {
        audience.filter { it != ValidAudiences.PROVIDER.descriptor }
    } else {
        audience
    }
}

private fun resolveTimestamps(
    createdAt: ZonedDateTime,
    lastUpdated: ZonedDateTime,
    metatags: List<String>,
    fylke: String?
): Pair<ZonedDateTime?, ZonedDateTime?> {
    val showBothTimestamps = ValidMetatags.NYHET.descriptor in metatags
    val showNoTimestamps = fylke.isNullOrBlank() && metatags.none { it in metatagsWithModifiedTime }

    return when {
        showBothTimestamps -> Pair(createdAt, lastUpdated.takeIf { createdAt != lastUpdated })
        showNoTimestamps -> Pair(null, null)
        else -> Pair(null, lastUpdated)
    }
}