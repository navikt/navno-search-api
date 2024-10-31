package no.nav.navnosearchapi.service

import io.kotest.assertions.json.shouldEqualJson
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.mapper.AggregationsMapper
import no.nav.navnosearchapi.service.mapper.HighlightMapper
import no.nav.navnosearchapi.service.mapper.SearchResultMapper
import no.nav.navnosearchapi.utils.readJsonFile
import org.junit.jupiter.api.Test
import org.opensearch.data.client.orhlc.NativeSearchQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.data.elasticsearch.core.TotalHitsRelation

class CompleteSearchServiceTest {

    @Test
    fun `standard søk skal bruke forventet query`() {
        val querySlot = captureSearchQuery()

        searchService.search(
            Params(
                ord = "søketerm",
                preferredLanguage = NORWEGIAN_BOKMAAL
            )
        )

        with(querySlot.captured) {
            query.toString() shouldEqualJson readJsonFile("/json/standard-query.json")
            filter.toString() shouldEqualJson readJsonFile("/json/standard-filter.json")
            aggregations.toString() shouldEqualJson readJsonFile("/json/standard-aggregations.json")
            highlightBuilder.toString() shouldEqualJson readJsonFile("/json/standard-highlight-builder.json")
        }
    }

    @Test
    fun `frasesøk skal bruke forventet query`() {
        val querySlot = captureSearchQuery()

        searchService.search(
            Params(
                ord = "\"dette er en frase\"",
                preferredLanguage = NORWEGIAN_BOKMAAL
            )
        )

        with(querySlot.captured) {
            query.toString() shouldEqualJson readJsonFile("/json/phrase-query.json")
            filter.toString() shouldEqualJson readJsonFile("/json/standard-filter.json")
            aggregations.toString() shouldEqualJson readJsonFile("/json/standard-aggregations.json")
            highlightBuilder.toString() shouldEqualJson readJsonFile("/json/phrase-highlight-builder.json")
        }
    }

    companion object {
        val mockedOperations = mockk<ElasticsearchOperations>()
        val searchService = CompleteSearchService(
            pageSize = 20,
            searchResultMapper = SearchResultMapper(AggregationsMapper(), HighlightMapper()),
            searchClient = SearchClient(mockedOperations)
        )

        val mockedSearchHits: SearchHits<Content> = SearchHitsImpl(
            /* totalHits = */ 0,
            /* totalHitsRelation = */ TotalHitsRelation.EQUAL_TO,
            /* maxScore = */ 0f,
            /* scrollId = */ null,
            /* pointInTimeId = */ null,
            /* searchHits = */ emptyList(),
            /* aggregations = */ null,
            /* suggest = */ null,
            /* searchShardStatistics = */ null
        )

        private fun captureSearchQuery(): CapturingSlot<NativeSearchQuery> {
            val querySlot = slot<NativeSearchQuery>()

            every {
                mockedOperations.search(capture(querySlot), Content::class.java)
            } answers {
                mockedSearchHits
            }

            return querySlot
        }
    }
}