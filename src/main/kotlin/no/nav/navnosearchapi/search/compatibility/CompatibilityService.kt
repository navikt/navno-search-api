package no.nav.navnosearchapi.search.compatibility

import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.compatibility.filters.fasettFilters
import no.nav.navnosearchapi.search.compatibility.filters.fylkeFilters
import no.nav.navnosearchapi.search.compatibility.filters.innholdFilters
import no.nav.navnosearchapi.search.compatibility.filters.nyheterFilters
import no.nav.navnosearchapi.search.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import org.opensearch.index.query.QueryBuilder
import org.springframework.stereotype.Component

@Component
class CompatibilityService(val searchResultMapper: SearchResultMapper) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toFilters(f: String?, uf: List<String>?, daterange: Int?): List<QueryBuilder> {
        return when (f) {
            FASETT_INNHOLD -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD]?.filters
                } else {
                    uf.flatMap { innholdFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_ENGLISH -> fasettFilters[ENGLISH]?.filters
            FASETT_NYHETER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_NYHETER]?.filters
                } else {
                    uf.flatMap { nyheterFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_ANALYSER_OG_FORSKNING -> fasettFilters[FASETT_ANALYSER_OG_FORSKNING]?.filters
            FASETT_STATISTIKK -> fasettFilters[FASETT_STATISTIKK]?.filters
            FASETT_INNHOLD_FRA_FYLKER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD_FRA_FYLKER]?.filters
                } else {
                    uf.flatMap { fylkeFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_FILER -> fasettFilters[FASETT_FILER]?.filters
            else -> emptyList()
        } ?: emptyList()
    }
}