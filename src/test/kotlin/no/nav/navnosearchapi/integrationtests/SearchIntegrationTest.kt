package no.nav.navnosearchapi.integrationtests

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import no.nav.navnosearchapi.handler.ErrorResponse
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.utils.FacetKeys
import no.nav.navnosearchapi.service.utils.UnderFacetKeys
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

        result.total shouldBe 1L
        result.isMore.shouldBeFalse()
    }

    @Test
    fun testSearchForText() {
        val result = restTemplate.getForEntity<SearchResult>(searchUri(TEXT_TERM)).body!!

        result.total shouldBe 1L
    }

    @Test
    fun testSearchForPhrase() {
        val result = restTemplate.getForEntity<SearchResult>(searchUri(PHRASE_TERM, f = FacetKeys.ARBEIDSGIVER)).body!!

        result.total shouldBe 1L
    }

    @Test
    fun testSearchWithFasettFilter() {
        val result =
            restTemplate.getForEntity<SearchResult>(
                searchUri(
                    ord = TEXT_TERM,
                    f = FacetKeys.INNHOLD_FRA_FYLKER
                )
            ).body!!

        result.total shouldBe 3L
    }

    @Test
    fun testSearchWithUnderfasettFilter() {
        val result = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = TEXT_TERM, f = FacetKeys.PRIVATPERSON, uf = listOf(UnderFacetKeys.INFORMASJON))
        ).body!!

        result.total shouldBe 1L
    }

    @Test
    fun testSearchWithMissingParameter() {
        val response = restTemplate.getForEntity<ErrorResponse>(searchUri(ord = TEXT_TERM, s = null))

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body?.message shouldBe "Påkrevd request parameter mangler: s"
    }

    companion object {
        private const val TEXT_TERM = "title"
        private const val PHRASE_TERM = "\"Sixth title\""
        private const val EMPTY_TERM = ""
    }
}