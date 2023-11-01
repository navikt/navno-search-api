package no.nav.navnosearchapi.service.search

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.AUTOCOMPLETE
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.ID
import no.nav.navnosearchadminapi.common.constants.INGRESS_WILDCARD
import no.nav.navnosearchadminapi.common.constants.IS_FILE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.LAST_UPDATED
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.MISSING_FYLKE
import no.nav.navnosearchadminapi.common.constants.TEXT_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TITLE_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TOTAL_COUNT
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.service.search.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.utils.now
import no.nav.navnosearchapi.utils.sevenDaysAgo
import no.nav.navnosearchapi.utils.thirtyDaysAgo
import no.nav.navnosearchapi.utils.twelveMonthsAgo
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.BoolQueryBuilder
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
        filters: BoolQueryBuilder,
        aggregations: List<AbstractAggregationBuilder<*>> = aggregations(),
        mapCustomAggregations: Boolean = false,
        sort: Sort? = null
    ): ContentSearchPage {
        val pageRequest = PageRequest.of(page, pageSize)

        val searchQuery = NativeSearchQueryBuilder()
            .withQuery(baseQuery(term))
            .withFilter(filters)
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

        private const val UNFRAGMENTED = 0
        private const val MAX_FRAGMENT_SIZE = 200

        private val highlightFields = listOf(
            HighlightField(
                TITLE_WILDCARD,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG)
                    .withPostTags(BOLD_POSTTAG)
                    .withNumberOfFragments(UNFRAGMENTED)
                    .build()
            ),
            HighlightField(
                INGRESS_WILDCARD,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG)
                    .withPostTags(BOLD_POSTTAG)
                    .withNumberOfFragments(UNFRAGMENTED)
                    .build()
            ),
            HighlightField(
                TEXT_WILDCARD,
                HighlightFieldParameters.HighlightFieldParametersBuilder()
                    .withPreTags(BOLD_PRETAG)
                    .withPostTags(BOLD_POSTTAG)
                    .withFragmentSize(MAX_FRAGMENT_SIZE)
                    .build()
            )
        )

        private val highlightQuery = HighlightQuery(
            Highlight(highlightFields),
            ContentDao::class.java
        )
    }
}