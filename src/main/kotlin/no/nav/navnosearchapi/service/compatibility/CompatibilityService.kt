package no.nav.navnosearchapi.service.compatibility

import joinClausesToSingleQuery
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.filters.fasettFilters
import no.nav.navnosearchapi.service.compatibility.filters.fylkeFilters
import no.nav.navnosearchapi.service.compatibility.filters.innholdFilters
import no.nav.navnosearchapi.service.compatibility.filters.nyheterFilters
import no.nav.navnosearchapi.service.compatibility.filters.tidsperiodeFilters
import no.nav.navnosearchapi.service.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.utils.ENGLISH
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CompatibilityService(val searchResultMapper: SearchResultMapper) {

    val logger: Logger = LoggerFactory.getLogger(CompatibilityService::class.java)

    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toFilterQuery(f: String?, uf: List<String>?, daterange: Int?): BoolQueryBuilder {
        return joinClausesToSingleQuery(
            mustClauses = listOf(
                activeFasettFilterQuery(f, uf),
                activeTidsperiodeFilterQuery(daterange)
            )
        )
    }

    fun aggregations(f: String?, uf: List<String>?): List<FilterAggregationBuilder> {
        return (fasettFilters.values + innholdFilters.values + nyheterFilters.values + fylkeFilters.values).map {
            AggregationBuilders.filter(it.name, it.filterQuery)
        } + tidsperiodeFilters.values.map {
            AggregationBuilders.filter(
                it.name,
                joinClausesToSingleQuery(mustClauses = listOf(activeFasettFilterQuery(f, uf), it.filterQuery))
            )
        }
    }

    fun activeFasettFilterQuery(f: String?, uf: List<String>?): BoolQueryBuilder {
        return when (f) {
            FASETT_INNHOLD -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD]?.filterQuery!!
                } else {
                    joinClausesToSingleQuery(shouldClauses = uf.map { innholdFilters[it]!!.filterQuery })
                }
            }

            FASETT_ENGLISH -> fasettFilters[ENGLISH]!!.filterQuery
            FASETT_NYHETER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_NYHETER]!!.filterQuery
                } else {
                    joinClausesToSingleQuery(shouldClauses = uf.map { nyheterFilters[it]!!.filterQuery })
                }
            }

            FASETT_ANALYSER_OG_FORSKNING -> fasettFilters[FASETT_ANALYSER_OG_FORSKNING]!!.filterQuery
            FASETT_STATISTIKK -> fasettFilters[FASETT_STATISTIKK]!!.filterQuery
            FASETT_INNHOLD_FRA_FYLKER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD_FRA_FYLKER]!!.filterQuery
                } else {
                    joinClausesToSingleQuery(shouldClauses = uf.map { fylkeFilters[it]!!.filterQuery })
                }
            }

            FASETT_FILER -> fasettFilters[FASETT_FILER]!!.filterQuery
            else -> BoolQueryBuilder()
        }
    }

    fun activeTidsperiodeFilterQuery(daterange: Int?): BoolQueryBuilder {
        return tidsperiodeFilters[daterange.toString()]!!.filterQuery
    }

    fun term(term: String): String {
        if (isSkjemanummer(term)) {
            return toExactSkjemanummerTerm(term)
        }
        return term
    }

    private fun toExactSkjemanummerTerm(term: String): String {
        val digits = term.filter { it.isDigit() }

        if (digits.length != SKJEMANUMMER_DIGITS_LENGTH) {
            logger.warn("Skjemanummer kunne ikke formateres: $term. Bruker uformatert søkeord.")
            return term
        }

        val firstPart = digits.substring(0, 2)
        val secondPart = digits.substring(2, 4)
        val thirdPart = digits.substring(4, 6)

        return "\"NAV $firstPart-$secondPart.$thirdPart\""
    }

    private fun isSkjemanummer(term: String): Boolean {
        return term.matches(skjemanummerRegex)
    }

    companion object {
        private const val SKJEMANUMMER_DIGITS_LENGTH = 6
        private const val SKJEMANUMMER_FORMAT = "^((NAV|nav).?)?([0-9]{2}).?([0-9]{2}).?([0-9]{2})$"
        private val skjemanummerRegex = Regex(SKJEMANUMMER_FORMAT)
    }
}