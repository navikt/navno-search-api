package no.nav.navnosearchapi.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.service.search.SearchHelper
import no.nav.navnosearchapi.service.search.filteredQuery
import no.nav.navnosearchapi.service.search.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.service.search.searchAllTextQuery
import org.springframework.stereotype.Service


@Service
class SearchService(
    val searchHelper: SearchHelper,
    val objectMapper: ObjectMapper,
) {
    fun searchAllText(term: String, maalgruppe: List<String>?, page: Int): ContentSearchPage {
        val query = if (isInQuotes(term)) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(term)
        }

        if (maalgruppe != null) {
            val filteredQuery = filteredQuery(query, filtersAsJson(maalgruppe))
            return searchHelper.search(filteredQuery, page)
        }

        return searchHelper.search(query, page)
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith('"') && term.endsWith('"')
    }

    private fun filtersAsJson(maalgruppe: List<String>?): String {
        return objectMapper.writeValueAsString(mapOf(MAALGRUPPE_KEYWORD to maalgruppe))
    }

    companion object {
        private const val MAALGRUPPE_KEYWORD = "maalgruppe.keyword"
    }
}