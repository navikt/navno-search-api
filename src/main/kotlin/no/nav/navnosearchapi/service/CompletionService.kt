package no.nav.navnosearchapi.service

import no.nav.navnosearchadminapi.common.constants.SORT_BY_DATE
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.CompletionResponse
import no.nav.navnosearchapi.service.utils.activeFasettFilterQuery
import no.nav.navnosearchapi.service.utils.activePreferredLanguageFilterQuery
import no.nav.navnosearchapi.service.utils.isInQuotes
import no.nav.navnosearchapi.service.utils.value
import org.opensearch.index.query.BoolQueryBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CompletionService(
    val searchClient: SearchClient,
) {
    fun search(params: Params): CompletionResponse {
        val isMatchPhraseQuery = isInQuotes(params.ord)
        return searchClient.searchCompletion(
            term = params.ord,
            isMatchPhraseQuery = isMatchPhraseQuery,
            filters = filters(params.f, params.uf, params.preferredLanguage),
            sort = Sort.by(Sort.Direction.DESC, SORT_BY_DATE).takeIf { params.s == 1 },
            pageRequest = PageRequest.of(params.page, 10)
        ).let { searchPage ->
            CompletionResponse(searchPage.searchHits.searchHits.map { it.content.title.value()!! })
        }
    }

    fun filters(f: String, uf: List<String>, preferredLanguage: String): BoolQueryBuilder {
        return BoolQueryBuilder()
            .must(activeFasettFilterQuery(f, uf))
            .must(activePreferredLanguageFilterQuery(preferredLanguage))
    }
}
