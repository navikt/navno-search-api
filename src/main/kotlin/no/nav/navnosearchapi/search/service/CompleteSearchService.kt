package no.nav.navnosearchapi.search.service

import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.search.controller.Params
import no.nav.navnosearchapi.search.dto.SearchResult
import no.nav.navnosearchapi.search.factory.SearchQueryFactory
import no.nav.navnosearchapi.search.mapper.toSearchResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CompleteSearchService(
    @Value("\${opensearch.page-size}") val pageSize: Int,
    val searchClient: SearchClient,
) {
    fun search(params: Params): SearchResult {
        val query = SearchQueryFactory.createBuilder(params = params, includeAggregations = true)
        val result = searchClient.searchForPage(query, PageRequest.of(params.page, pageSize))
        return toSearchResult(params, result)
    }
}