package no.nav.navnosearchapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.mapper.outbound.ContentSearchPageMapper
import no.nav.navnosearchapi.service.search.SearchHelper
import no.nav.navnosearchapi.service.search.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.service.search.searchAllTextQuery
import no.nav.navnosearchapi.service.search.searchAsYouTypeQuery
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Service


@Service
class SearchService(
    val searchHelper: SearchHelper,
    val objectMapper: ObjectMapper,
    val mapper: ContentSearchPageMapper
) {
    fun searchAllText(term: String, audience: List<String>?, page: Int): ContentSearchPage {
        val query = if (isInQuotes(term)) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(term)
        }

        val searchResult = searchHelper.searchPage(query, page, audience?.let { filtersAsJson(it) }, highlightFields)

        return mapper.toContentSearchPage(searchResult, suggestions(term))
    }

    private fun suggestions(term: String): List<String?> {
        val query = searchAsYouTypeQuery(term.removeSurrounding("\""))
        val searchResult = searchHelper.search(query, MAX_SUGGESTIONS)
        return searchResult.map { hit -> hit.content.name.searchAsYouType }.toList()
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith('"') && term.endsWith('"')
    }

    private fun filtersAsJson(audience: List<String>?): String {
        return objectMapper.writeValueAsString(mapOf(AUDIENCE_KEYWORD to audience))
    }

    companion object {
        private const val AUDIENCE_KEYWORD = "audience"
        private const val MAX_SUGGESTIONS = 3

        val highlightFields = listOf("name.*", "ingress.*", "text.*").map { HighlightField(it) }
    }
}