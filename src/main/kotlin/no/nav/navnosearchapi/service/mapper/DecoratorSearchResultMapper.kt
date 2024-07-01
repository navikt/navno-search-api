package no.nav.navnosearchapi.service.mapper

import no.nav.navnosearchapi.client.dto.ContentSearchHit
import no.nav.navnosearchapi.client.dto.ContentSearchPage
import no.nav.navnosearchapi.service.Params
import no.nav.navnosearchapi.service.dto.DecoratorSearchHit
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
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