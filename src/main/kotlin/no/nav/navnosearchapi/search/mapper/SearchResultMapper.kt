package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.SearchHit
import no.nav.navnosearchapi.search.dto.SearchResult
import no.nav.navnosearchapi.search.utils.isInQuotes
import no.nav.navnosearchapi.search.utils.languageSubfieldValue
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.springframework.data.elasticsearch.core.AggregationsContainer
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import org.springframework.data.elasticsearch.core.SearchHit as OpensearchSearchHit

@Component
class SearchResultMapper(val aggregationsMapper: AggregationsMapper, val highlightMapper: HighlightMapper) {
    fun toSearchResult(params: Params, searchPage: SearchPage<Content>): SearchResult {
        return SearchResult(
            page = params.page,
            s = params.s,
            preferredLanguage = params.preferredLanguage,
            isMore = !searchPage.isLast,
            word = params.ord,
            total = searchPage.totalElements,
            fasettKey = params.f,
            aggregations = searchPage.searchHits.aggregations?.asMap()?.let { aggregations ->
                aggregationsMapper.toAggregations(aggregations, params)
            },
            hits = searchPage.searchHits.searchHits.map { toHit(it, params.ord.isInQuotes()) },
        )
    }

    private fun <T> AggregationsContainer<T>.asMap(): Map<String, Long> {
        return (this as OpenSearchAggregations).aggregations().associate { it.name to (it as Filter).docCount }
    }

    private fun toHit(searchHit: OpensearchSearchHit<Content>, isMatchPhraseQuery: Boolean): SearchHit {
        with(searchHit.content) {
            resolveTimestamps(createdAt, lastUpdated, metatags, fylke).let { (publishedTime, modifiedTime) ->
                return SearchHit(
                    displayName = title.languageSubfieldValue(language),
                    href = href,
                    highlight = highlightMapper.toHighlight(searchHit, isMatchPhraseQuery),
                    modifiedTime = modifiedTime,
                    publishedTime = publishedTime,
                    audience = toAudience(audience),
                    language = language,
                    type = type,
                    score = searchHit.score,
                )
            }
        }
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

    companion object {
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
    }
}