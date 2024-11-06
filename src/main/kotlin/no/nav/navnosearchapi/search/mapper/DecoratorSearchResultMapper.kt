package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.DecoratorSearchHit
import no.nav.navnosearchapi.search.dto.DecoratorSearchResult
import no.nav.navnosearchapi.search.utils.isInQuotes
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class DecoratorSearchResultMapper(
    val highlightMapper: HighlightMapper,
) {
    fun toSearchResult(
        params: Params,
        result: SearchPage<Content>,
    ): DecoratorSearchResult {
        return DecoratorSearchResult(
            preferredLanguage = params.preferredLanguage,
            word = params.ord,
            total = result.totalElements,
            hits = result.searchHits.searchHits.map { toHit(it, params.ord.isInQuotes()) },
        )
    }

    private fun toHit(searchHit: SearchHit<Content>, isMatchPhraseQuery: Boolean): DecoratorSearchHit {
        return DecoratorSearchHit(
            displayName = searchHit.content.title.value,
            href = searchHit.content.href,
            highlight = highlightMapper.toHighlight(searchHit, isMatchPhraseQuery),
        )
    }
}