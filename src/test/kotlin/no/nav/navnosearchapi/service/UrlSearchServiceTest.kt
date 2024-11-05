package no.nav.navnosearchapi.service

import io.kotest.assertions.json.shouldEqualJson
import io.mockk.mockk
import no.nav.navnosearchapi.common.client.SearchClient
import no.nav.navnosearchapi.searchurl.factory.UrlSearchQueryFactory
import no.nav.navnosearchapi.searchurl.service.UrlSearchService
import no.nav.navnosearchapi.utils.captureSearchQuery
import no.nav.navnosearchapi.utils.readJsonFile
import org.junit.jupiter.api.Test
import org.springframework.data.elasticsearch.core.ElasticsearchOperations

class UrlSearchServiceTest {
    val mockedOperations = mockk<ElasticsearchOperations>()

    val searchService = UrlSearchService(
        searchQueryService = UrlSearchQueryFactory(),
        searchClient = SearchClient(mockedOperations)
    )

    @Test
    fun `standard søk skal bruke forventet query`() {
        val querySlot = captureSearchQuery(mockedOperations)

        searchService.search("https://dummy.url")

        querySlot.captured.query.toString() shouldEqualJson readJsonFile("/search-url-queries/standard-query.json")
    }
}