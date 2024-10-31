package no.nav.navnosearchapi.utils

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import no.nav.navnosearchadminapi.common.model.Content
import org.opensearch.data.client.orhlc.NativeSearchQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.data.elasticsearch.core.TotalHitsRelation

private val mockedSearchHits: SearchHits<Content> = SearchHitsImpl(
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

fun captureSearchQuery(mockedOperations: ElasticsearchOperations): CapturingSlot<NativeSearchQuery> {
    val querySlot = slot<NativeSearchQuery>()

    every {
        mockedOperations.search(capture(querySlot), Content::class.java)
    } answers {
        mockedSearchHits
    }

    return querySlot
}
