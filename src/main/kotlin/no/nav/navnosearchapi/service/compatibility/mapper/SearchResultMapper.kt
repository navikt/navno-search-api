package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.SearchHit
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
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
            hits = result.hits.map { toHit(it, params.f) },
            autoComplete = result.suggestions,
        )
    }

    private fun toHit(searchHit: ContentSearchHit, facet: String): SearchHit {
        // Filtrer ut overordnet Provider-audience hvis den inneholder mer presist audience
        fun toAudience(audience: List<String>) = if (audience.any { it in providerSubaudiences }) {
            audience.filter { it != ValidAudiences.PROVIDER.descriptor }
        } else audience

        return SearchHit(
            displayName = searchHit.title,
            href = searchHit.href,
            highlight = toHighlight(searchHit, facet),
            modifiedTime = searchHit.lastUpdated.toString(),
            audience = toAudience(searchHit.audience),
            language = searchHit.language,
            type = searchHit.type,
            score = searchHit.score,
        )
    }

    private fun toHighlight(searchHit: ContentSearchHit, facet: String): String {
        fun isInnhold() = facet in innholdFacets
        fun isTabell() = facet == FacetKeys.STATISTIKK && searchHit.ingress.isBlank()
        fun isKontor() = searchHit.type in arrayOf(ValidTypes.KONTOR.descriptor, ValidTypes.KONTOR_LEGACY.descriptor)

        return when {
            isKontor() -> toIngressHighlight(searchHit.ingress)
            isInnhold() -> toIngressHighlight(searchHit.highlight.ingress.firstOrNull() ?: searchHit.ingress)
            isTabell() -> TABELL
            else -> searchHit.highlight.ingress.firstOrNull()?.let { toIngressHighlight(it) }
                ?: searchHit.highlight.text.firstOrNull()?.let { toTextHighlight(it) }
                ?: toIngressHighlight(searchHit.ingress)
        }
    }

    private fun toTextHighlight(highlight: String): String {
        return highlight + CUTOFF_POSTFIX
    }

    private fun toIngressHighlight(highlight: String): String {
        return if (highlight.length > HIGHLIGHT_MAX_LENGTH) {
            highlight.substring(0, HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
        } else highlight
    }

    companion object {
        private const val HIGHLIGHT_MAX_LENGTH = 220
        private const val CUTOFF_POSTFIX = " (...)"

        private const val TABELL = "Tabell"

        private val innholdFacets = listOf(FacetKeys.PRIVATPERSON, FacetKeys.ARBEIDSGIVER, FacetKeys.SAMARBEIDSPARTNER)
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