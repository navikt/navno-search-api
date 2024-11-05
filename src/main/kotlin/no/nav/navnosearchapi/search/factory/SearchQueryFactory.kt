package no.nav.navnosearchapi.search.factory

import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchapi.common.utils.applyFilters
import no.nav.navnosearchapi.common.utils.applyWeighting
import no.nav.navnosearchapi.search.config.metatagToWeight
import no.nav.navnosearchapi.search.config.termsToOverride
import no.nav.navnosearchapi.search.config.typeToWeight
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.factory.queries.highlightBuilder
import no.nav.navnosearchapi.search.factory.queries.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.search.factory.queries.searchAllTextQuery
import no.nav.navnosearchapi.search.utils.activeFasettFilterQuery
import no.nav.navnosearchapi.search.utils.activePreferredLanguageFilterQuery
import no.nav.navnosearchapi.search.utils.isInQuotes
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class SearchQueryFactory {
    fun createBuilder(
        params: Params,
        aggregations: List<AbstractAggregationBuilder<*>>? = null,
    ): NativeSearchQueryBuilder {
        val baseQuery = baseQuery(params.ord)
        return NativeSearchQueryBuilder().apply {
            withQuery(
                baseQuery.applyFilters(preAggregationFilters(params.preferredLanguage))
                    .applyWeighting(TYPE, typeToWeight)
                    .applyWeighting(METATAGS, metatagToWeight)
            )
            withFilter(postAggregationFilters(params.f, params.uf))
            withHighlightBuilder(highlightBuilder(baseQuery, params.ord.isInQuotes()))
            withTrackTotalHits(true)
            apply {
                aggregations?.let { withAggregations(it) }
                sort?.let { withSort(it) }
                if (params.s == 1) withSort(Sort.by(Sort.Direction.DESC, SORT_BY_DATE))
            }
        }
    }

    private fun baseQuery(term: String): QueryBuilder {
        return resolveTerm(term).let { (resolvedTerm, skjemanummer) ->
            when {
                term.isBlank() -> MatchAllQueryBuilder()
                resolvedTerm.isBlank() && skjemanummer != null -> searchAllTextForPhraseQuery(skjemanummer)
                term.isInQuotes() -> searchAllTextForPhraseQuery(term)
                else -> searchAllTextQuery(resolvedTerm, skjemanummer)
            }
        }
    }

    private fun preAggregationFilters(preferredLanguage: String?) = BoolQueryBuilder().apply {
        if (preferredLanguage != null) {
            this.must(activePreferredLanguageFilterQuery(preferredLanguage))
        }
    }

    private fun postAggregationFilters(
        f: String,
        uf: List<String>
    ) = BoolQueryBuilder().must(activeFasettFilterQuery(f, uf))

    private fun resolveTerm(term: String): Pair<String, String?> {
        return term.replace(Regex("\\s+"), "")
            .let { termsToOverride[it] ?: term }
            .let { extractSkjemanummerIfPresent(it) }
    }

    private fun extractSkjemanummerIfPresent(term: String): Pair<String, String?> {
        return skjemanummerRegex.find(term)?.let {
            term.replace(it.value, "") to "NAV ${it.groupValues[1]}-${it.groupValues[2]}.${it.groupValues[3]}"
        } ?: (term to null)
    }

    companion object {
        private const val SKJEMANUMMER_FORMAT = """(?:NAV|nav)?.?([0-9]{2}).?([0-9]{2}).?([0-9]{2})"""
        private val skjemanummerRegex = Regex(SKJEMANUMMER_FORMAT)

    }
}