package no.nav.navnosearchapi.integrationtests

import io.kotest.assertions.assertSoftly
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
    fun `søk med tom term skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(searchUri(EMPTY_TERM))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
            isMore.shouldBeFalse()
        }
    }

    @Test
    fun `søk med tekst-term skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(searchUri(TEXT_TERM))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
        }
    }

    @Test
    fun `søk med frase-term skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(searchUri(ord = PHRASE_TERM, f = FacetKeys.ARBEIDSGIVER))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
        }
    }

    @Test
    fun `søk med fasett skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = TEXT_TERM, f = FacetKeys.INNHOLD_FRA_FYLKER)
        )

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 3L
        }
    }

    @Test
    fun `søk med underfasett skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = TEXT_TERM, f = FacetKeys.PRIVATPERSON, uf = listOf(UnderFacetKeys.INFORMASJON))
        )

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
        }
    }

    @Test
    fun `søk med manglende påkrevd parameter skal gi 400`() {
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