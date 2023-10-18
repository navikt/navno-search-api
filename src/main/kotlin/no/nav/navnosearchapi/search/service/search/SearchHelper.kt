package no.nav.navnosearchapi.search.service.search

import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.utils.AUTOCOMPLETE
import no.nav.navnosearchapi.common.utils.INGRESS_WILDCARD
import no.nav.navnosearchapi.common.utils.TEXT_WILDCARD
import no.nav.navnosearchapi.common.utils.TITLE_WILDCARD
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.suggest.SuggestBuilder
import org.opensearch.search.suggest.completion.CompletionSuggestionBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.query.HighlightQuery
import org.springframework.data.elasticsearch.core.query.highlight.Highlight
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters
import org.springframework.stereotype.Component

@Component
class SearchHelper(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val operations: ElasticsearchOperations,
) {
    fun searchPage(
        term: String,
        page: Int,
        filters: List<QueryBuilder>,
        aggregations: List<AbstractAggregationBuilder<*>>,
        sort: Sort? = null
    ): SearchPage<ContentDao> {
        val pageRequest = PageRequest.of(page, pageSize)

        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(baseQuery(term))
            .withFilter(filterQuery(filters))
            .withPageable(pageRequest)
            .withHighlightQuery(highlightQuery)
            .withAggregations(aggregations)
            .withSuggestBuilder(
                SuggestBuilder().addSuggestion(
                    AUTOCOMPLETE,
                    CompletionSuggestionBuilder(AUTOCOMPLETE).prefix(term).skipDuplicates(true).size(3)
                )
            )
            .withTrackTotalHits(true)

        sort?.let { searchQuery.withSort(it) }

        val searchHits = operations.search(searchQuery.build(), ContentDao::class.java)
        return SearchHitSupport.searchPageFor(searchHits, pageRequest)
    }

    private fun baseQuery(term: String): QueryBuilder {
        return if (term.isBlank()) {
            MatchAllQueryBuilder()
        } else if (isInQuotes(term)) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(term)
        }
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith(QUOTE) && term.endsWith(QUOTE)
    }

    companion object {
        private const val QUOTE = '"'
        private const val BOLD_PRETAG = "<b>"
        private const val BOLD_POSTTAG = "</b>"

        private val highlightFields = listOf(TITLE_WILDCARD, INGRESS_WILDCARD, TEXT_WILDCARD).map {
            HighlightField(
                it,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG).withPostTags(BOLD_POSTTAG).build()
            )
        }

        private val highlightQuery = HighlightQuery(
            Highlight(highlightFields),
            ContentDao::class.java
        )
    }
}