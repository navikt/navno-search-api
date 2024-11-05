package no.nav.navnosearchapi.service

import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.filters.Filter
import no.nav.navnosearchapi.service.filters.facets.fasettFilters
import no.nav.navnosearchapi.service.mapper.SearchResultMapper
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CompleteSearchService(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val searchResultMapper: SearchResultMapper,
    val searchClient: SearchClient,
) {
    private val aggregations = aggregations()

    fun search(params: Params): SearchResult {
        val searchPage = searchClient.search(
            params = params,
            aggregations = aggregations,
            pageRequest = PageRequest.of(params.page, pageSize)
        )
        return searchResultMapper.toSearchResult(
            params = params,
            searchPage = searchPage
        )
    }

    private fun aggregations(): List<FilterAggregationBuilder> {
        val allFacetsAndUnderfacets = fasettFilters + fasettFilters.flatMap(Filter::underFacets)
        return allFacetsAndUnderfacets.map { AggregationBuilders.filter(it.aggregationName, it.filterQuery) }
    }
}