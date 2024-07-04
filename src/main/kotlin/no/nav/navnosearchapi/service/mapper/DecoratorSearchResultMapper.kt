package no.nav.navnosearchapi.service.mapper

import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.DecoratorSearchHit
import no.nav.navnosearchapi.service.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.utils.languageSubfieldValue
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
        isMatchPhraseQuery: Boolean
    ): DecoratorSearchResult {
        return DecoratorSearchResult(
            preferredLanguage = params.preferredLanguage,
            word = params.ord,
            total = result.totalElements,
            hits = result.searchHits.searchHits.map { toHit(it, isMatchPhraseQuery) },
        )
    }

    private fun toHit(searchHit: SearchHit<Content>, isMatchPhraseQuery: Boolean): DecoratorSearchHit {
        return DecoratorSearchHit(
            displayName = searchHit.content.title.languageSubfieldValue(searchHit.content.language),
            href = searchHit.content.href,
            highlight = highlightMapper.toHighlight(searchHit, isMatchPhraseQuery),
        )
    }
}