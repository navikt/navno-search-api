package no.nav.navnosearchapi

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.utils.additionalTestData
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

class AdminIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        setupIndex()
    }

    @Test
    fun testFetchingContent() {
        val result: ResponseEntity<SearchHitsImpl<Content>> = restTemplate.getForEntity("${host()}/content/testapp")

        assertThat(result.body?.totalHits).isEqualTo(10L)
    }

    @Test
    fun testSavingContent() {
        val content: ResponseEntity<List<Content>> = restTemplate.exchange(
            "${host()}/content/testapp",
            HttpMethod.POST,
            HttpEntity(additionalTestData),
        )

        await().untilCallTo { indexCount() } matches { count -> count == 11L }

        val savedContent: Content = content.body?.first()!!

        assertThat(indexCount()).isEqualTo(11L)
        assertThat(operations.exists(savedContent.id, indexCoordinates))
    }

    @Test
    fun testDeletingContent() {
        val deletedId = "1"
        restTemplate.delete("${host()}/content/testapp/$deletedId")

        await().untilCallTo { indexCount() } matches { count -> count == 9L }

        assertThat(indexCount()).isEqualTo(9L)
        assertThat(!operations.exists(deletedId, indexCoordinates))
    }
}