package no.nav.navnosearchapi.search.service

import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.AUDIENCE
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
import no.nav.navnosearchapi.common.utils.TITLE_WILDCARD
import no.nav.navnosearchapi.common.utils.TOTAL_COUNT
import no.nav.navnosearchapi.common.utils.now
import no.nav.navnosearchapi.common.utils.sevenDaysAgo
import no.nav.navnosearchapi.common.utils.thirtyDaysAgo
import no.nav.navnosearchapi.common.utils.twelveMonthsAgo
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.search.service.search.SearchHelper
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.QueryBuilders
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.stereotype.Service


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
        mapCustomAggregations: Boolean = false,
        sort: Sort? = null,
    ): ContentSearchPage {
        val searchResult = searchHelper.searchPage(
            term,
            page,
            filters,
            aggregations,
            highlightFields,
            sort
        )

        return mapper.toContentSearchPage(searchResult, mapCustomAggregations)
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
            AggregationBuilders.dateRange(DATE_RANGE_LAST_12_MONTHS).addRange(twelveMonthsAgo(), now()).field(LAST_UPDATED),
            AggregationBuilders.dateRange(DATE_RANGE_OLDER_THAN_12_MONTHS).addUnboundedTo(twelveMonthsAgo())
                .field(LAST_UPDATED),
        )
    }
}