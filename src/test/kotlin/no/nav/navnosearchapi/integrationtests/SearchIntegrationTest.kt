package no.nav.navnosearchapi.integrationtests

import no.nav.navnosearchapi.handler.ErrorResponse
import no.nav.navnosearchapi.service.compatibility.dto.Aggregations
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON
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
    fun testSearchWithEmptyTerm() {
        val result = restTemplate.getForEntity<SearchResult>(searchUri(EMPTY_TERM)).body!!

        assertThat(result.total).isEqualTo(0L)
        assertThat(result.isMore).isFalse()
        assertThat(result.autoComplete).isEmpty()
    }

    @Test
    fun testSearchForText() {
        val result = restTemplate.getForEntity<SearchResult>(searchUri(TEXT_TERM)).body!!

        assertThat(result.total).isEqualTo(7L)
    }

    @Test
    fun testSearchForPhrase() {
        val result = restTemplate.getForEntity<SearchResult>(searchUri(PHRASE_TERM)).body!!

        assertThat(result.total).isEqualTo(1L)
    }

    @Test
    fun testSearchWithFasettFilter() {
        val result =
            restTemplate.getForEntity<SearchResult>(
                searchUri(
                    ord = TEXT_TERM,
                    f = FASETT_INNHOLD_FRA_FYLKER.toInt()
                )
            ).body!!

        assertThat(result.total).isEqualTo(3L)
    }

    @Test
    fun testSearchWithUnderfasettFilter() {
        val result = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = TEXT_TERM, f = FASETT_INNHOLD.toInt(), uf = listOf(UNDERFASETT_INFORMASJON))
        ).body!!

        assertThat(result.total).isEqualTo(7L)
    }

    @Test
    fun testSearchWithTidsperiodeFilter() {
        val result = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = TEXT_TERM, daterange = TIDSPERIODE_LAST_30_DAYS.toInt())
        ).body!!

        assertThat(result.total).isEqualTo(2L)
    }

    @Test
    fun testSearchWithMissingParameter() {
        val response = restTemplate.getForEntity<ErrorResponse>(searchUri(ord = TEXT_TERM, daterange = null))

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body?.message).isEqualTo("Påkrevd request parameter mangler: daterange")
    }

    private fun fasettCount(aggregations: Aggregations, key: String): Long? {
        return aggregations.fasetter.buckets?.find { it.key == key }?.docCount
    }

    private fun tidsperiodeCount(aggregations: Aggregations, key: String): Long? {
        return aggregations.tidsperiode.buckets.find { it.key == key }?.docCount
    }

    companion object {
        private const val TEXT_TERM = "title"
        private const val PHRASE_TERM = "\"Sixth title\""
        private const val EMPTY_TERM = ""
    }
}