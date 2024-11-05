package no.nav.navnosearchapi.search.service

import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.SearchResult
import no.nav.navnosearchapi.search.factory.SearchQueryFactory
import no.nav.navnosearchapi.search.filters.Filter
import no.nav.navnosearchapi.search.filters.facets.fasettFilters
import no.nav.navnosearchapi.search.mapper.SearchResultMapper
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CompleteSearchService(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val searchResultMapper: SearchResultMapper,
    val searchQueryFactory: SearchQueryFactory,
    val searchClient: SearchClient,
) {
    private val aggregations = aggregations()

    fun search(params: Params): SearchResult {
        val query = searchQueryFactory.createBuilder(params, aggregations)
        val result = searchClient.searchForPage(query, PageRequest.of(params.page, pageSize))
        return searchResultMapper.toSearchResult(params, result)
    }

    private fun aggregations(): List<FilterAggregationBuilder> {
        val allFacetsAndUnderfacets = fasettFilters + fasettFilters.flatMap(Filter::underFacets)
        return allFacetsAndUnderfacets.map { AggregationBuilders.filter(it.aggregationName, it.filterQuery) }
    }
}