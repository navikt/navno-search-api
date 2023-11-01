package no.nav.navnosearchapi

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.IS_FILE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.LAST_UPDATED_FROM
import no.nav.navnosearchadminapi.common.constants.LAST_UPDATED_TO
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchapi.handler.ErrorResponse
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.utils.AGDER
import no.nav.navnosearchapi.utils.ARBEIDSGIVER
import no.nav.navnosearchapi.utils.HINDI
import no.nav.navnosearchapi.utils.PRIVATPERSON
import no.nav.navnosearchapi.utils.SAMARBEIDSPARTNER
import no.nav.navnosearchapi.utils.STATISTIKK
import no.nav.navnosearchapi.utils.nowMinus10Days
import no.nav.navnosearchapi.utils.nowMinus50Days
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import java.time.format.DateTimeFormatter

class SearchIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        setupIndex()
    }

    @Test
    fun testSearchWithEmptyTerm() {
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(EMPTY_TERM)).body!!

        assertThat(result.totalElements).isEqualTo(10L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.aggregations.audience?.get(PRIVATPERSON)).isEqualTo(4L)
        assertThat(result.aggregations.audience?.get(ARBEIDSGIVER)).isEqualTo(4L)
        assertThat(result.aggregations.audience?.get(SAMARBEIDSPARTNER)).isEqualTo(4L)

        assertThat(result.aggregations.language?.get(NORWEGIAN_BOKMAAL)).isEqualTo(3L)
        assertThat(result.aggregations.language?.get(ENGLISH)).isEqualTo(3L)
        assertThat(result.aggregations.language?.get(HINDI)).isEqualTo(4L)

        assertThat(result.aggregations.fylke?.get(AGDER)).isEqualTo(3L)
        assertThat(result.aggregations.metatags?.get(STATISTIKK)).isEqualTo(3L)
        assertThat(result.aggregations.isFile).isEqualTo(3L)

        assertThat(result.aggregations.dateRangeAggregations?.get(DATE_RANGE_LAST_7_DAYS)).isEqualTo(2L)
        assertThat(result.aggregations.dateRangeAggregations?.get(DATE_RANGE_LAST_30_DAYS)).isEqualTo(4L)
        assertThat(result.aggregations.dateRangeAggregations?.get(DATE_RANGE_LAST_12_MONTHS)).isEqualTo(8L)
        assertThat(result.aggregations.dateRangeAggregations?.get(DATE_RANGE_OLDER_THAN_12_MONTHS)).isEqualTo(2L)
    }

    @Test
    fun testSearchForText() {
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(TEXT_TERM)).body!!

        assertThat(result.totalElements).isEqualTo(10L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.suggestions).isNotEmpty()
        assertThat(result.hits[0].highlight.title).isNotEmpty()
        assertThat(result.hits[0].highlight.ingress).isNotEmpty()
        assertThat(result.hits[0].highlight.text).isNotEmpty()
    }

    @Test
    fun testSearchForPhrase() {
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(PHRASE_TERM)).body!!

        assertThat(result.totalElements).isEqualTo(1L)
        assertThat(result.totalPages).isEqualTo(1L)

        assertThat(result.suggestions).isNotEmpty()
        assertThat(result.hits[0].highlight.title).isNotEmpty()
        assertThat(result.hits[0].highlight.ingress).isEmpty()
        assertThat(result.hits[0].highlight.text).isEmpty()
    }

    @Test
    fun testSearchWithAudienceFilter() {
        val filter = mapOf(AUDIENCE to listOf(PRIVATPERSON))
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(4L)
    }

    @Test
    fun testSearchWithLanguageFilter() {
        val filter = mapOf(LANGUAGE to listOf(NORWEGIAN_BOKMAAL))
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(3L)
    }

    @Test
    fun testSearchWithFylkeFilter() {
        val filter = mapOf(FYLKE to listOf(AGDER))
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(3L)
    }

    @Test
    fun testSearchWithMetatagsFilter() {
        val filter = mapOf(METATAGS to listOf(STATISTIKK))
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(3L)
    }

    @Test
    fun testSearchWithIsFileFilter() {
        val filter = mapOf(IS_FILE to listOf(true.toString()))
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(3L)
    }

    @Test
    fun testSearchWithRangeFilter() {
        val filter = mapOf(
            LAST_UPDATED_FROM to listOf(nowMinus50Days.format(DateTimeFormatter.ISO_INSTANT)),
            LAST_UPDATED_TO to listOf(nowMinus10Days.format(DateTimeFormatter.ISO_INSTANT))
        )
        val result = restTemplate.getForEntity<ContentSearchPage>(searchUrl(term = EMPTY_TERM, filters = filter)).body!!

        assertThat(result.totalElements).isEqualTo(6L)
    }

    @Test
    fun testSearchWithMissingParameter() {
        val response = restTemplate.getForEntity<ErrorResponse>("${host()}/content/search")

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Påkrevd request parameter mangler: term")
    }

    companion object {
        private const val TEXT_TERM = "First title"
        private const val PHRASE_TERM = "\"Second title\""
        private const val EMPTY_TERM = ""
    }
}