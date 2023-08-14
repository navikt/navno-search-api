package no.nav.navnosearchapi

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.utils.additionalTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class AdminIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        setupIndex()
    }

    @Test
    fun testFetchingContent() {
        val result = restTemplate.getForEntity<ContentSearchPage>("${host()}/content/testapp?page=0")

        assertThat(result.body?.totalElements).isEqualTo(10L)
    }

    @Test
    fun testSavingContent() {
        val content: ResponseEntity<List<ContentDao>> = restTemplate.exchange(
            "${host()}/content/testapp",
            HttpMethod.POST,
            HttpEntity(additionalTestData),
        )

        val savedContent: ContentDao = content.body?.first()!!

        operations.indexOps(indexCoordinates).refresh()

        assertThat(indexCount()).isEqualTo(11L)
        assertThat(operations.exists(savedContent.id, indexCoordinates))
    }

    @Test
    fun testDeletingContent() {
        val deletedId = "1"
        restTemplate.delete("${host()}/content/testapp/$deletedId")

        operations.indexOps(indexCoordinates).refresh()

        assertThat(indexCount()).isEqualTo(9L)
        assertThat(!operations.exists(deletedId, indexCoordinates))
    }

    @Test
    fun testDeletingContentForMissingApp() {
        val deletedId = "1"
        val appName = "missing-app"
        val response: ResponseEntity<String> =
            restTemplate.exchange("${host()}/content/$appName/$deletedId", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isEqualTo("Fant ingen index for app: $appName")
    }
}