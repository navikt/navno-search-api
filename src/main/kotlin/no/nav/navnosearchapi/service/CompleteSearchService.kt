package no.nav.navnosearchapi.service

import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.filters.Filter
import no.nav.navnosearchapi.service.filters.facets.fasettFilters
import no.nav.navnosearchapi.service.mapper.SearchResultMapper
import no.nav.navnosearchapi.service.utils.activeFasettFilterQuery
import no.nav.navnosearchapi.service.utils.activePreferredLanguageFilterQuery
import no.nav.navnosearchapi.service.utils.isInQuotes
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CompleteSearchService(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val searchResultMapper: SearchResultMapper,
    val searchClient: SearchClient,
) {
    fun search(params: Params): SearchResult {
        val isMatchPhraseQuery = isInQuotes(params.ord)
        val searchPage = searchClient.search(
            term = params.ord,
            isMatchPhraseQuery = isMatchPhraseQuery,
            filters = postAggregationFilters(params.f, params.uf),
            preAggregationFilters = preAggregationFilters(params.preferredLanguage),
            aggregations = aggregations(),
            sort = Sort.by(Sort.Direction.DESC, SORT_BY_DATE).takeIf { params.s == 1 },
            pageRequest = PageRequest.of(params.page, pageSize)
        )
        return searchResultMapper.toSearchResult(params, searchPage, isMatchPhraseQuery)

    }

    private fun preAggregationFilters(preferredLanguage: String?) = BoolQueryBuilder().apply {
        if (preferredLanguage != null) {
            this.must(activePreferredLanguageFilterQuery(preferredLanguage))
        }
    }

    private fun postAggregationFilters(
        f: String,
        uf: List<String>
    ) = BoolQueryBuilder().must(activeFasettFilterQuery(f, uf))


    private fun aggregations(): List<FilterAggregationBuilder> {
        val allFacetsAndUnderfacets = fasettFilters + fasettFilters.flatMap(Filter::underFacets)
        return allFacetsAndUnderfacets.map { AggregationBuilders.filter(it.aggregationName, it.filterQuery) }
    }
}