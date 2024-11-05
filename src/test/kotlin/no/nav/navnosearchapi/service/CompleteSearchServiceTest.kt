package no.nav.navnosearchapi.service

import io.kotest.assertions.json.shouldEqualJson
import io.mockk.mockk
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.mapper.AggregationsMapper
import no.nav.navnosearchapi.service.mapper.HighlightMapper
import no.nav.navnosearchapi.service.mapper.SearchResultMapper
import no.nav.navnosearchapi.utils.captureSearchQuery
import no.nav.navnosearchapi.utils.readJsonFile
import org.junit.jupiter.api.Test
import org.springframework.data.elasticsearch.core.ElasticsearchOperations

class CompleteSearchServiceTest {
    val mockedOperations = mockk<ElasticsearchOperations>()

    val searchService = CompleteSearchService(
        pageSize = 20,
        searchResultMapper = SearchResultMapper(AggregationsMapper(), HighlightMapper()),
        searchClient = SearchClient(mockedOperations)
    )

    @Test
    fun `standard søk skal bruke forventet query`() {
        val querySlot = captureSearchQuery(mockedOperations)

        searchService.search(Params(ord = "søketerm"))

        with(querySlot.captured) {
            query.toString() shouldEqualJson readJsonFile("/search-queries/standard-query.json")
            filter.toString() shouldEqualJson readJsonFile("/search-queries/standard-filter.json")
            aggregations.toString() shouldEqualJson readJsonFile("/search-queries/standard-aggregations.json")
            highlightBuilder.toString() shouldEqualJson readJsonFile("/search-queries/standard-highlight-builder.json")
        }
    }

    @Test
    fun `frasesøk skal bruke forventet query`() {
        val querySlot = captureSearchQuery(mockedOperations)

        searchService.search(Params(ord = "\"dette er en frase\""))

        with(querySlot.captured) {
            query.toString() shouldEqualJson readJsonFile("/search-queries/phrase-query.json")
            filter.toString() shouldEqualJson readJsonFile("/search-queries/standard-filter.json")
            aggregations.toString() shouldEqualJson readJsonFile("/search-queries/standard-aggregations.json")
            highlightBuilder.toString() shouldEqualJson readJsonFile("/search-queries/phrase-highlight-builder.json")
        }
    }

    @Test
    fun `søk med skjemanummer skal bruke forventet query`() {
        val querySlot = captureSearchQuery(mockedOperations)

        searchService.search(Params(ord = "her er et skjemanummer: NAV 09-35.01"))

        with(querySlot.captured) {
            query.toString() shouldEqualJson readJsonFile("/search-queries/skjemanummer-query.json")
            filter.toString() shouldEqualJson readJsonFile("/search-queries/standard-filter.json")
            aggregations.toString() shouldEqualJson readJsonFile("/search-queries/standard-aggregations.json")
            highlightBuilder.toString() shouldEqualJson readJsonFile("/search-queries/skjemanummer-highlight-builder.json")
        }
    }
}