package no.nav.navnosearchapi

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.utils.additionalTestData
import no.nav.navnosearchapi.utils.additionalTestDataAsMapWithMissingIngress
import no.nav.navnosearchapi.utils.createId
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
        val result = restTemplate.getForEntity<Map<String, Any>>("${host()}/content/testTeam?page=0")

        assertThat(result.body?.get("totalElements")).isEqualTo(10)
    }

    @Test
    fun testSavingContent() {
        val content: ResponseEntity<List<ContentDto>> = restTemplate.exchange(
            "${host()}/content/testTeam",
            HttpMethod.POST,
            HttpEntity(additionalTestData),
        )

        val savedContent: ContentDto = content.body?.first()!!

        operations.indexOps(ContentDao::class.java).refresh()

        assertThat(indexCount()).isEqualTo(11L)
        assertThat(operations.exists(createId("testTeam", savedContent.id), ContentDao::class.java))
    }

    @Test
    fun testSavingContentWithMissingRequiredField() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "${host()}/content/testTeam",
            HttpMethod.POST,
            HttpEntity(additionalTestDataAsMapWithMissingIngress),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isEqualTo("Påkrevd felt mangler: ingress")
    }

    @Test
    fun testSavingContentWithNonSupportedLanguage() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "${host()}/content/testTeam",
            HttpMethod.POST,
            HttpEntity(listOf(additionalTestData[0].copy(language = "unsupported"))),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isEqualTo("Validering feilet: language må være en av følgende gyldige verdier: [en, no]")
    }

    @Test
    fun testDeletingContent() {
        val deletedId = "1"
        val response: ResponseEntity<String> =
            restTemplate.exchange("${host()}/content/testTeam/$deletedId", HttpMethod.DELETE)

        operations.indexOps(ContentDao::class.java).refresh()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(indexCount()).isEqualTo(9L)
        assertThat(!operations.exists(deletedId, ContentDao::class.java))
    }

    @Test
    fun testDeletingContentForMissingApp() {
        val deletedId = "1"
        val teamName = "missing-team"
        val response: ResponseEntity<String> =
            restTemplate.exchange("${host()}/content/$teamName/$deletedId", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isEqualTo("Dokument med ekstern id $deletedId finnes ikke for team $teamName")
    }
}