package no.nav.navnosearchapi

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.utils.TEAM_NAME
import no.nav.navnosearchapi.utils.additionalTestData
import no.nav.navnosearchapi.utils.additionalTestDataAsMapWithMissingIngress
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
        val response = restTemplate.getForEntity<Map<String, Any>>("${host()}/content/$TEAM_NAME?page=0")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.get("totalElements")).isEqualTo(10)
    }

    @Test
    fun testSavingContent() {
        val response: ResponseEntity<List<ContentDto>> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
            HttpMethod.POST,
            HttpEntity(additionalTestData),
        )

        val savedContent: ContentDto = response.body?.first()!!


        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(indexCount()).isEqualTo(11L)
        assertThat(!repository.existsById("$TEAM_NAME-${savedContent.id}"))
    }

    @Test
    fun testSavingContentWithMissingRequiredField() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
            HttpMethod.POST,
            HttpEntity(additionalTestDataAsMapWithMissingIngress),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isEqualTo("Påkrevd felt mangler: ingress")
    }

    @Test
    fun testSavingContentWithNonSupportedLanguage() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
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
            restTemplate.exchange("${host()}/content/$TEAM_NAME/$deletedId", HttpMethod.DELETE)


        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(indexCount()).isEqualTo(9L)
        assertThat(!repository.existsById("$TEAM_NAME-$deletedId"))
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