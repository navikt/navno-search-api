package no.nav.navnosearchapi.search.service

import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.AUDIENCE
import no.nav.navnosearchapi.common.utils.AUTOCOMPLETE_KEYWORD
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.common.utils.INGRESS_WILDCARD
import no.nav.navnosearchapi.common.utils.IS_FILE
import no.nav.navnosearchapi.common.utils.LANGUAGE
import no.nav.navnosearchapi.common.utils.LAST_UPDATED
import no.nav.navnosearchapi.common.utils.METATAGS
import no.nav.navnosearchapi.common.utils.TEXT_WILDCARD
import no.nav.navnosearchapi.common.utils.TITLE_WILDCARD
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.search.service.search.SearchHelper
import no.nav.navnosearchapi.search.service.search.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.search.service.search.searchAllTextQuery
import no.nav.navnosearchapi.search.service.search.searchAsYouTypeQuery
import org.opensearch.index.query.AbstractQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.QueryBuilders
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Service
import java.time.ZonedDateTime


@Service
class SearchService(
    val searchHelper: SearchHelper,
    val mapper: ContentSearchPageMapper,
) {
    val highlightFields = listOf(TITLE_WILDCARD, INGRESS_WILDCARD, TEXT_WILDCARD).map { HighlightField(it) }

    fun search(
        term: String,
        page: Int,
        filters: List<QueryBuilder>,
        aggregations: List<AbstractAggregationBuilder<*>> = aggregations(),
        sort: Sort? = null,
    ): ContentSearchPage {
        val searchResult = searchHelper.searchPage(
            baseQuery(term),
            page,
            filters,
            aggregations,
            highlightFields,
            sort
        )

        val suggestions = suggestions(term, filters)

        return mapper.toContentSearchPage(searchResult, suggestions)
    }

    private fun baseQuery(term: String): AbstractQueryBuilder<*> {
        return if (term.isBlank()) {
            MatchAllQueryBuilder()
        } else if (isInQuotes(term)) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(term)
        }
    }

    private fun aggregations(): List<AbstractAggregationBuilder<*>> {
        val now = ZonedDateTime.now()
        val sevenDaysAgo = now.minusDays(7)
        val thirtyDaysAgo = now.minusDays(30)
        val twelveMonthsAgo = now.minusMonths(12)

        return listOf(
            AggregationBuilders.terms(AUDIENCE).field(AUDIENCE),
            AggregationBuilders.terms(LANGUAGE).field(LANGUAGE).size(20),
            AggregationBuilders.terms(FYLKE).field(FYLKE).size((ValidFylker.entries.size)),
            AggregationBuilders.terms(METATAGS).field(METATAGS).size(ValidMetatags.entries.size),
            AggregationBuilders.filter(IS_FILE, QueryBuilders.termQuery(IS_FILE, true)),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_7_DAYS).addRange(sevenDaysAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_30_DAYS).addRange(thirtyDaysAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_12_MONTHS).addRange(twelveMonthsAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_OLDER_THAN_12_MONTHS).addUnboundedTo(twelveMonthsAgo)
                .field(LAST_UPDATED),
        )
    }

    private fun suggestions(term: String, filters: List<QueryBuilder>): List<String?> {
        val query = searchAsYouTypeQuery(term)
        val searchResult = searchHelper.search(
            baseQuery = query,
            filters = filters,
            collapseField = AUTOCOMPLETE_KEYWORD
        )
        return searchResult.map { hit -> hit.content.autocomplete }.toList()
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith('"') && term.endsWith('"')
    }
}