package no.nav.navnosearchapi.service.search

import no.nav.navnosearchadminapi.common.constants.AUTOCOMPLETE
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchapi.service.search.config.termsToOverride
import no.nav.navnosearchapi.service.search.config.typeToWeight
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.service.search.dto.SearchUrlResponse
import no.nav.navnosearchapi.service.search.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.service.search.queries.highlightBuilder
import no.nav.navnosearchapi.service.search.queries.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.service.search.queries.searchAllTextQuery
import no.nav.navnosearchapi.service.search.queries.searchUrlQuery
import no.nav.navnosearchapi.service.search.utils.applyFilters
import no.nav.navnosearchapi.service.search.utils.applyWeighting
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.suggest.SuggestBuilder
import org.opensearch.search.suggest.completion.CompletionSuggestionBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.stereotype.Component

@Component
class SearchService(
    val operations: ElasticsearchOperations,
    val mapper: ContentSearchPageMapper,
) {
    fun search(
        term: String,
        pageSize: Int,
        page: Int,
        filters: BoolQueryBuilder,
        preAggregationFilters: BoolQueryBuilder? = null,
        aggregations: List<AbstractAggregationBuilder<*>>? = null,
        sort: Sort? = null
    ): ContentSearchPage {
        val pageRequest = PageRequest.of(page, pageSize)

        val isMatchPhraseQuery = isInQuotes(term)
        val baseQuery = baseQuery(term, isMatchPhraseQuery)

        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(baseQuery.applyFilters(preAggregationFilters).applyWeighting(TYPE, typeToWeight))
            .withFilter(filters)
            .withPageable(pageRequest)
            .withHighlightBuilder(highlightBuilder(baseQuery, isMatchPhraseQuery))
            .withSuggestBuilder(
                SuggestBuilder().addSuggestion(
                    AUTOCOMPLETE,
                    CompletionSuggestionBuilder(AUTOCOMPLETE).prefix(term).skipDuplicates(true).size(3)
                )
            )
            .withTrackTotalHits(true)
            .apply { if (aggregations != null) this.withAggregations(aggregations) }

        sort?.let { searchQuery.withSort(it) }

        val searchHits = operations.search(searchQuery.build(), ContentDao::class.java)
        val searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest)

        return mapper.toContentSearchPage(searchPage, isMatchPhraseQuery)
    }

    fun searchUrl(term: String): SearchUrlResponse {
        val searchQuery =
            NativeSearchQueryBuilder().withQuery(searchUrlQuery(term).applyWeighting(TYPE, typeToWeight))
        val searchHits = operations.search(searchQuery.build(), ContentDao::class.java)
        return SearchUrlResponse(suggestion = searchHits.searchHits.firstOrNull()?.content?.href)
    }

    private fun baseQuery(term: String, isMatchPhraseQuery: Boolean): QueryBuilder {
        val (resolvedTerm, skjemanummer) = resolveTerm(term)
        return when {
            term.isBlank() -> MatchAllQueryBuilder()
            resolvedTerm.isBlank() && skjemanummer != null -> searchAllTextForPhraseQuery(skjemanummer)
            isMatchPhraseQuery -> searchAllTextForPhraseQuery(term)
            else -> searchAllTextQuery(resolvedTerm, skjemanummer)
        }
    }

    private fun resolveTerm(term: String): Pair<String, String?> {
        return term.replace(whitespace, "")
            .let { termsToOverride[it] ?: term }
            .let { extractSkjemanummerIfPresent(it) }
    }

    private fun extractSkjemanummerIfPresent(term: String): Pair<String, String?> {
        return skjemanummerRegex.find(term)?.let {
            term.replace(it.value, "") to "NAV ${it.groupValues[1]}-${it.groupValues[2]}.${it.groupValues[3]}"
        } ?: (term to null)
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith(QUOTE) && term.endsWith(QUOTE)
    }

    companion object {
        private const val QUOTE = '"'
        private const val SKJEMANUMMER_FORMAT = """(?:NAV|nav)?.?([0-9]{2}).?([0-9]{2}).?([0-9]{2})"""
        private val skjemanummerRegex = Regex(SKJEMANUMMER_FORMAT)
        val whitespace = "\\s+".toRegex()
    }
}