package no.nav.navnosearchapi.service

import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.filters.analyseFilters
import no.nav.navnosearchapi.service.filters.arbeidsgiverFilters
import no.nav.navnosearchapi.service.filters.fasettFilters
import no.nav.navnosearchapi.service.filters.fylkeFilters
import no.nav.navnosearchapi.service.filters.privatpersonFilters
import no.nav.navnosearchapi.service.filters.samarbeidspartnerFilters
import no.nav.navnosearchapi.service.filters.statistikkFilters
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
        return searchClient.search(
            term = params.ord,
            isMatchPhraseQuery = isMatchPhraseQuery,
            filters = postAggregationFilters(params.f, params.uf),
            preAggregationFilters = preAggregationFilters(params.preferredLanguage),
            aggregations = aggregations(params.f, params.uf),
            sort = Sort.by(Sort.Direction.DESC, SORT_BY_DATE).takeIf { params.s == 1 },
            pageRequest = PageRequest.of(params.page, pageSize)
        ).let { searchPage ->
            searchResultMapper.toSearchResult(params, searchPage, isMatchPhraseQuery)
        }
    }

    fun preAggregationFilters(preferredLanguage: String?): BoolQueryBuilder {
        return BoolQueryBuilder().apply {
            if (preferredLanguage != null) {
                this.must(activePreferredLanguageFilterQuery(preferredLanguage))
            }
        }
    }

    fun postAggregationFilters(f: String, uf: List<String>): BoolQueryBuilder {
        return BoolQueryBuilder().must(activeFasettFilterQuery(f, uf))
    }

    fun aggregations(f: String, uf: List<String>): List<FilterAggregationBuilder> {
        return (fasettFilters.values +
                privatpersonFilters.values +
                arbeidsgiverFilters.values +
                samarbeidspartnerFilters.values +
                statistikkFilters.values +
                analyseFilters.values +
                fylkeFilters.values).map { AggregationBuilders.filter(it.aggregationName, it.filterQuery) }
    }
}