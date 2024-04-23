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
            highlight = searchHit.toHighlight(),
        )
    }
}