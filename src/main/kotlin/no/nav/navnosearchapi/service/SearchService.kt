package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.mapper.outbound.ContentSearchPageMapper
import no.nav.navnosearchapi.service.search.Filters
import no.nav.navnosearchapi.service.search.SearchHelper
import no.nav.navnosearchapi.service.search.rangeQuery
import no.nav.navnosearchapi.service.search.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.service.search.searchAllTextQuery
import no.nav.navnosearchapi.service.search.searchAsYouTypeQuery
import no.nav.navnosearchapi.service.search.termsQuery
import no.nav.navnosearchapi.utils.AUDIENCE
import no.nav.navnosearchapi.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.utils.FYLKE
import no.nav.navnosearchapi.utils.IS_FILE
import no.nav.navnosearchapi.utils.LANGUAGE
import no.nav.navnosearchapi.utils.LAST_UPDATED
import no.nav.navnosearchapi.utils.METATAGS
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.QueryBuilders
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime


@Service
class SearchService(
    val searchHelper: SearchHelper,
    val mapper: ContentSearchPageMapper,
) {
    fun searchAllText(term: String, page: Int, filters: Filters): ContentSearchPage {
        val baseQuery = if (isInQuotes(term)) {
            searchAllTextForPhraseQuery(term)
        } else {
            searchAllTextQuery(term)
        }

        val searchResult =
            searchHelper.searchPage(baseQuery, page, filterList(filters), aggregations(), highlightFields)

        return mapper.toContentSearchPage(searchResult, suggestions(term))
    }

    private fun aggregations(): List<AbstractAggregationBuilder<*>> {
        val now = ZonedDateTime.now()
        val sevenDaysAgo = now.minusDays(7)
        val thirtyDaysAgo = now.minusDays(30)
        val twelveMonthsAgo = now.minusMonths(12)

        return listOf(
            AggregationBuilders.terms(AUDIENCE).field(AUDIENCE),
            AggregationBuilders.terms(LANGUAGE).field(LANGUAGE),
            AggregationBuilders.terms(FYLKE).field(FYLKE),
            AggregationBuilders.terms(METATAGS).field(METATAGS),
            AggregationBuilders.filter(IS_FILE, QueryBuilders.termQuery(IS_FILE, true)),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_7_DAYS).addRange(sevenDaysAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_30_DAYS).addRange(thirtyDaysAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_LAST_12_MONTHS).addRange(twelveMonthsAgo, now).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_OLDER_THAN_12_MONTHS).addUnboundedTo(twelveMonthsAgo)
                .field(LAST_UPDATED),
        )
    }

    private fun filterList(filters: Filters): List<QueryBuilder> {
        val filterList = mutableListOf<QueryBuilder>()

        filters.audience?.let { filterList.add(termsQuery(AUDIENCE, it)) }
        filters.language?.let { filterList.add(termsQuery(LANGUAGE, it)) }
        filters.fylke?.let { filterList.add(termsQuery(FYLKE, it)) }
        filters.metatags?.let { filterList.add(termsQuery(METATAGS, it)) }
        filters.isFile?.let { filterList.add(termsQuery(IS_FILE, it)) }

        if (filters.lastUpdatedFrom != null || filters.lastUpdatedTo != null) {
            filterList.add(
                rangeQuery(
                    LAST_UPDATED,
                    filters.lastUpdatedFrom?.atZone(ZoneId.systemDefault()),
                    filters.lastUpdatedTo?.atZone(ZoneId.systemDefault())
                )
            )
        }

        return filterList
    }

    private fun suggestions(term: String): List<String?> {
        val query = searchAsYouTypeQuery(term)
        val searchResult = searchHelper.search(query, MAX_SUGGESTIONS)
        return searchResult.map { hit -> hit.content.title.searchAsYouType }.toList()
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith('"') && term.endsWith('"')
    }

    companion object {
        private const val MAX_SUGGESTIONS = 3

        val highlightFields = listOf("title.*", "ingress.*", "text.*").map { HighlightField(it) }
    }
}