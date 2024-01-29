package no.nav.navnosearchapi.service.search

import no.nav.navnosearchadminapi.common.constants.AUTOCOMPLETE
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.service.search.dto.SearchUrlResponse
import no.nav.navnosearchapi.service.search.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.service.search.queries.applyFilters
import no.nav.navnosearchapi.service.search.queries.applyTypeWeighting
import no.nav.navnosearchapi.service.search.queries.highlightBuilder
import no.nav.navnosearchapi.service.search.queries.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.service.search.queries.searchAllTextQuery
import no.nav.navnosearchapi.service.search.queries.searchUrlQuery
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
            .withQuery(baseQuery.applyFilters(preAggregationFilters).applyTypeWeighting())
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
        val searchQuery = NativeSearchQueryBuilder().withQuery(searchUrlQuery(term).applyTypeWeighting())
        val searchHits = operations.search(searchQuery.build(), ContentDao::class.java)
        return SearchUrlResponse(suggestion = searchHits.searchHits.firstOrNull()?.content?.href)
    }

    private fun baseQuery(term: String, isMatchPhraseQuery: Boolean): QueryBuilder {
        return if (term.isBlank()) {
            MatchAllQueryBuilder()
        } else if (isMatchPhraseQuery) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(resolveTerm(term))
        }
    }

    private fun resolveTerm(term: String): String {
        val strippedTerm = term.replace(whitespace, "")
        return termsMap[strippedTerm] ?: term
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith(QUOTE) && term.endsWith(QUOTE)
    }


    companion object {
        private const val QUOTE = '"'
        val whitespace = "\\s+".toRegex()

        private val termsMap = mapOf("kontakt" to "kontakt oss")
    }
}