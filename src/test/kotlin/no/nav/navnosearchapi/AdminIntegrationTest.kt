package no.nav.navnosearchapi

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.utils.initialTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.http.ResponseEntity

class ContentRepositoryIntegrationTests : IntegrationTest() {

    @BeforeEach
    fun setup() {
        val initialContent = initialTestData()

        runBlocking {
            operations.indexOps(indexCoordinates).delete()
            operations.indexOps(indexCoordinates).create()
            operations.save(initialContent, indexCoordinates)
            delay(1000)
        }
    }


    @Test
    fun testFetchingContent() {
        val result: ResponseEntity<SearchHitsImpl<Content>> = restTemplate.getForEntity("${host()}/content/testapp")

        assertThat(result.body?.totalHits).isEqualTo(10L)
    }

    @Test
    fun testSavingContent() {
        runBlocking {
            restTemplate.postForLocation(
                "${host()}/content/testapp",
                listOf(Content(
                    "11",
                    "https://eleventh.com",
                    "Eleventh name",
                    "Eleventh ingress",
                    "Eleventh text",
                    "Samarbeidspartner"
                ))
            )
            delay(1000)
        }

        assertThat(indexCount()).isEqualTo(11L)
    }

    @Test
    fun testDeletingContent() {
        runBlocking {
            restTemplate.delete("${host()}/content/testapp/1")
            delay(1000)
        }

        assertThat(indexCount()).isEqualTo(9L)
    }
}