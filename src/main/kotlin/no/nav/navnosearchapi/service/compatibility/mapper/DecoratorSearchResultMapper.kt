package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchapi.service.compatibility.Params
import no.nav.navnosearchapi.service.compatibility.dto.DecoratorSearchHit
import no.nav.navnosearchapi.service.compatibility.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import org.springframework.stereotype.Component

@Component
class DecoratorSearchResultMapper {
    fun toSearchResult(params: Params, result: ContentSearchPage): DecoratorSearchResult {
        return DecoratorSearchResult(
            audience = params.audience,
            preferredLanguage = params.preferredLanguage,
            word = params.ord,
            total = result.totalElements,
            hits = result.hits.map { toHit(it) },
        )
    }

    private fun toHit(searchHit: ContentSearchHit): DecoratorSearchHit {
        return DecoratorSearchHit(
            displayName = searchHit.title,
            href = searchHit.href,
            highlight = toHighlight(searchHit.ingress),
        )
    }

    private fun toHighlight(ingress: String): String {
        return if (ingress.length > HIGHLIGHT_MAX_LENGTH) {
            ingress.substring(0, HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
        } else ingress
    }

    companion object {
        private const val CUTOFF_POSTFIX = " (...)"
        private const val HIGHLIGHT_MAX_LENGTH = 200
    }
}