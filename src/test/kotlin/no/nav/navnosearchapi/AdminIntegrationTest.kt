package no.nav.navnosearchapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import no.nav.navnosearchapi.common.exception.handler.ErrorResponse
import no.nav.navnosearchapi.utils.TEAM_NAME
import no.nav.navnosearchapi.utils.additionalTestData
import no.nav.navnosearchapi.utils.additionalTestDataAsMapWithMissingIngress
import no.nav.navnosearchapi.utils.dummyContentDto
import no.nav.navnosearchapi.utils.mockedKodeverkResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity

class AdminIntegrationTest : AbstractIntegrationTest() {

    val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        setupIndex()
        stubFor(
            get(urlPathMatching("/kodeverk")).willReturn(
                aResponse().withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(mockedKodeverkResponse))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            )
        )
    }

    @Test
    fun testFetchingContent() {
        val response = restTemplate.getForEntity<Map<String, Any>>("${host()}/content/$TEAM_NAME?page=0")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.get("totalElements")).isEqualTo(10)
    }

    @Test
    fun testSavingContent() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
            HttpMethod.POST,
            HttpEntity(additionalTestData),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(indexCount()).isEqualTo(11L)
        assertThat(!repository.existsById("$TEAM_NAME-${additionalTestData.first().id}"))
    }

    @Test
    fun testSavingContentWithMissingRequiredField() {
        val response: ResponseEntity<ErrorResponse> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
            HttpMethod.POST,
            HttpEntity(additionalTestDataAsMapWithMissingIngress),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Påkrevd felt mangler: ingress")
    }

    @Test
    fun testSavingContentWithNonSupportedLanguage() {
        val response: ResponseEntity<ErrorResponse> = restTemplate.exchange(
            "${host()}/content/$TEAM_NAME",
            HttpMethod.POST,
            HttpEntity(listOf(dummyContentDto(language = "unsupported"))),
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Ugyldig språkkode: unsupported. Må være tobokstavs språkkode fra kodeverk-api.")
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
        val response: ResponseEntity<ErrorResponse> =
            restTemplate.exchange("${host()}/content/$teamName/$deletedId", HttpMethod.DELETE)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Dokument med ekstern id $deletedId finnes ikke for team $teamName")
    }
}