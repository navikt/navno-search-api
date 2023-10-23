package no.nav.navnosearchapi.search.search

import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.utils.AUDIENCE
import no.nav.navnosearchapi.common.utils.AUTOCOMPLETE
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.common.utils.ID
import no.nav.navnosearchapi.common.utils.INGRESS_WILDCARD
import no.nav.navnosearchapi.common.utils.IS_FILE
import no.nav.navnosearchapi.common.utils.LANGUAGE
import no.nav.navnosearchapi.common.utils.LAST_UPDATED
import no.nav.navnosearchapi.common.utils.METATAGS
import no.nav.navnosearchapi.common.utils.MISSING_FYLKE
import no.nav.navnosearchapi.common.utils.TEXT_WILDCARD
import no.nav.navnosearchapi.common.utils.TOTAL_COUNT
import no.nav.navnosearchapi.common.utils.now
import no.nav.navnosearchapi.common.utils.sevenDaysAgo
import no.nav.navnosearchapi.common.utils.thirtyDaysAgo
import no.nav.navnosearchapi.common.utils.twelveMonthsAgo
import no.nav.navnosearchapi.search.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.search.mapper.ContentSearchPageMapper
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.QueryBuilders
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.suggest.SuggestBuilder
import org.opensearch.search.suggest.completion.CompletionSuggestionBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.query.HighlightQuery
import org.springframework.data.elasticsearch.core.query.highlight.Highlight
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters
import org.springframework.stereotype.Component

@Component
class SearchService(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val operations: ElasticsearchOperations,
    val mapper: ContentSearchPageMapper,
) {
    fun search(
        term: String,
        page: Int,
        filters: List<QueryBuilder>,
        aggregations: List<AbstractAggregationBuilder<*>> = aggregations(),
        mapCustomAggregations: Boolean = false,
        sort: Sort? = null
    ): ContentSearchPage {
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
        val searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest)

        return mapper.toContentSearchPage(searchPage, mapCustomAggregations)
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

    private fun aggregations(): List<AbstractAggregationBuilder<*>> {
        return listOf(
            AggregationBuilders.cardinality(TOTAL_COUNT).field(ID),
            AggregationBuilders.terms(AUDIENCE).field(AUDIENCE),
            AggregationBuilders.terms(LANGUAGE).field(LANGUAGE).size(20),
            AggregationBuilders.terms(FYLKE).field(FYLKE).size((ValidFylker.entries.size)).missing(MISSING_FYLKE),
            AggregationBuilders.terms(METATAGS).field(METATAGS).size(ValidMetatags.entries.size),
            AggregationBuilders.filter(IS_FILE, QueryBuilders.termQuery(IS_FILE, true)),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_7_DAYS).addRange(sevenDaysAgo(), now()).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_30_DAYS).addRange(thirtyDaysAgo(), now()).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_12_MONTHS).addRange(twelveMonthsAgo(), now())
                .field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_OLDER_THAN_12_MONTHS).addUnboundedTo(twelveMonthsAgo())
                .field(LAST_UPDATED),
        )
    }

    companion object {
        private const val QUOTE = '"'
        private const val BOLD_PRETAG = "<b>"
        private const val BOLD_POSTTAG = "</b>"

        private const val INGRESS_HIGHLIGHT_NUM_FRAGMENTS = 0
        private const val TEXT_HIGHLIGHT_FRAGMENT_SIZE = 200

        private val highlightFields = listOf(
            HighlightField(
                INGRESS_WILDCARD,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG)
                    .withPostTags(BOLD_POSTTAG)
                    .withNumberOfFragments(INGRESS_HIGHLIGHT_NUM_FRAGMENTS)
                    .build()
            ),
            HighlightField(
                TEXT_WILDCARD,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG)
                    .withPostTags(BOLD_POSTTAG)
                    .withFragmentSize(TEXT_HIGHLIGHT_FRAGMENT_SIZE)
                    .build()
            )
        )

        private val highlightQuery = HighlightQuery(
            Highlight(highlightFields),
            ContentDao::class.java
        )
    }
}