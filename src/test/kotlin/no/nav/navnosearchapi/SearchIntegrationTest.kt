package no.nav.navnosearchapi

import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.exception.handler.ErrorResponse
import no.nav.navnosearchapi.utils.ARBEIDSGIVER
import no.nav.navnosearchapi.utils.PRIVATPERSON
import no.nav.navnosearchapi.utils.SAMARBEIDSPARTNER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

class SearchIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        setupIndex()
    }

    @Test
    fun testSearchForText() {
        val term = "First text"

        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term)).body!!

        assertThat(result.totalElements).isEqualTo(10L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.aggregations.audience[PRIVATPERSON]).isEqualTo(4L)
        assertThat(result.aggregations.audience[ARBEIDSGIVER]).isEqualTo(4L)
        assertThat(result.aggregations.audience[SAMARBEIDSPARTNER]).isEqualTo(4L)
    }

    @Test
    fun testSearchForTextWithAudienceFilter() {
        val term = "First text"

        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term, audience = PRIVATPERSON)).body!!

        assertThat(result.totalElements).isEqualTo(4L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.aggregations.audience[PRIVATPERSON]).isEqualTo(4L)
        assertThat(result.aggregations.audience[ARBEIDSGIVER]).isEqualTo(1L)
        assertThat(result.aggregations.audience[SAMARBEIDSPARTNER]).isEqualTo(1L)
    }

    @Test
    fun testSearchForPhrase() {
        val term = "\"Second text\""

        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term)).body!!

        assertThat(result.totalElements).isEqualTo(1L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.aggregations.audience[PRIVATPERSON]).isEqualTo(1L)
        assertThat(!result.aggregations.audience.keys.contains(ARBEIDSGIVER))
        assertThat(!result.aggregations.audience.keys.contains(SAMARBEIDSPARTNER))

    }

    @Test
    fun testSearchWithMissingParameter() {
        val response = restTemplate.getForEntity<ErrorResponse>("${host()}/content/search")

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Påkrevd request parameter mangler: term")
    }
}