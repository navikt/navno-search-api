package no.nav.navnosearchapi.service.mapper

import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.client.dto.ContentSearchHit
import no.nav.navnosearchapi.client.dto.ContentSearchPage
import no.nav.navnosearchapi.service.Params
import no.nav.navnosearchapi.service.dto.SearchHit
import no.nav.navnosearchapi.service.dto.SearchResult
import org.springframework.stereotype.Component

@Component
class SearchResultMapper(val aggregationsMapper: AggregationsMapper) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return SearchResult(
            page = params.page,
            s = params.s,
            preferredLanguage = params.preferredLanguage,
            isMore = result.totalPages > (result.pageNumber + 1),
            word = params.ord,
            total = result.totalElements,
            fasettKey = params.f,
            aggregations = aggregationsMapper.toAggregations(result.aggregations!!, params),
            hits = result.hits.map { toHit(it) },
            autoComplete = result.suggestions,
        )
    }

    private fun toHit(searchHit: ContentSearchHit): SearchHit {
        return SearchHit(
            displayName = searchHit.title,
            href = searchHit.href,
            highlight = searchHit.toHighlight(),
            modifiedTime = searchHit.modifiedTime,
            publishedTime = searchHit.publishedTime,
            audience = toAudience(searchHit.audience),
            language = searchHit.language,
            type = searchHit.type,
            score = searchHit.score,
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
    }
}