package no.nav.navnosearchapi.integrationtests

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.navnosearchapi.handler.ErrorResponse
import no.nav.navnosearchapi.service.dto.SearchResult
import no.nav.navnosearchapi.service.filters.FacetKeys
import no.nav.navnosearchapi.service.filters.UnderFacetKeys
import no.nav.navnosearchapi.utils.additionalTestData
import no.nav.navnosearchapi.utils.aggregationCount
import no.nav.navnosearchapi.utils.allUnderaggregationCounts
import no.nav.navnosearchapi.utils.analyserOgForskningDummyData
import no.nav.navnosearchapi.utils.arbeidsgiverDummyData
import no.nav.navnosearchapi.utils.generatedText
import no.nav.navnosearchapi.utils.innholdFraFylkerDummyData
import no.nav.navnosearchapi.utils.presseDummyData
import no.nav.navnosearchapi.utils.privatpersonDummyData
import no.nav.navnosearchapi.utils.samarbeidspartnerDummyData
import no.nav.navnosearchapi.utils.statistikkDummyData
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
    fun `søk med tom term skal returnere alt innhold`() {
        val response = restTemplate.getForEntity<SearchResult>(searchUri(ord = "", f = FacetKeys.PRIVATPERSON))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe privatpersonDummyData.size

            aggregationCount(FacetKeys.PRIVATPERSON) shouldBe privatpersonDummyData.size
            allUnderaggregationCounts(FacetKeys.PRIVATPERSON).forEach { it shouldBe generatedText.size }

            aggregationCount(FacetKeys.ARBEIDSGIVER) shouldBe arbeidsgiverDummyData.size
            allUnderaggregationCounts(FacetKeys.ARBEIDSGIVER).forEach { it shouldBe generatedText.size }

            aggregationCount(FacetKeys.SAMARBEIDSPARTNER) shouldBe samarbeidspartnerDummyData.size
            allUnderaggregationCounts(FacetKeys.SAMARBEIDSPARTNER).forEach { it shouldBe generatedText.size }

            aggregationCount(FacetKeys.PRESSE) shouldBe presseDummyData.size

            aggregationCount(FacetKeys.STATISTIKK) shouldBe statistikkDummyData.size
            allUnderaggregationCounts(FacetKeys.STATISTIKK).forEach { it shouldBe generatedText.size }

            aggregationCount(FacetKeys.ANALYSER_OG_FORSKNING) shouldBe analyserOgForskningDummyData.size
            allUnderaggregationCounts(FacetKeys.ANALYSER_OG_FORSKNING).forEach { it shouldBe generatedText.size }

            aggregationCount(FacetKeys.INNHOLD_FRA_FYLKER) shouldBe innholdFraFylkerDummyData.size
            allUnderaggregationCounts(FacetKeys.INNHOLD_FRA_FYLKER).forEach { it shouldBe generatedText.size }
        }
    }

    @Test
    fun `søk med tekst-term skal returnere riktig søkeresultat`() {
        val text = "dagpenger"

        repository.save(additionalTestData(title = text))
        val response = restTemplate.getForEntity<SearchResult>(searchUri(ord = text))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
            hits.first().displayName shouldBe text
        }
    }

    @Test
    fun `søk med fuzzy tekst-term skal returnere riktig søkeresultat`() {
        val text = "dagpenger"
        val fuzzyText = "dagpegner"

        repository.save(additionalTestData(title = text))
        val response = restTemplate.getForEntity<SearchResult>(searchUri(ord = fuzzyText))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
            hits.first().displayName shouldBe text
        }
    }

    @Test
    fun `søk med frase-term skal returnere riktig søkeresultat`() {
        val frase = "søknad om dagpenger"
        val fraseReversert = frase.split(" ").reversed().joinToString(" ")

        repository.saveAll(
            listOf(
                additionalTestData(title = "blabla $frase blabla"),
                additionalTestData(title = "blabla $fraseReversert blabla"),
            )
        )

        val response = restTemplate.getForEntity<SearchResult>(searchUri(ord = "\"$frase\""))

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe 1L
            hits.first().displayName shouldContain frase
        }
    }

    @Test
    fun `søk med fasett skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = "", f = FacetKeys.INNHOLD_FRA_FYLKER)
        )

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe innholdFraFylkerDummyData.size
        }
    }

    @Test
    fun `søk med underfasett skal returnere riktig søkeresultat`() {
        val response = restTemplate.getForEntity<SearchResult>(
            searchUri(ord = "", f = FacetKeys.PRIVATPERSON, uf = listOf(UnderFacetKeys.INFORMASJON))
        )

        response.statusCode shouldBe HttpStatus.OK
        assertSoftly(response.body!!) {
            total shouldBe generatedText.size
        }
    }

    @Test
    fun `søk med manglende påkrevd parameter skal gi 400`() {
        val response = restTemplate.getForEntity<ErrorResponse>(searchUri(ord = "text", s = null))

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body?.message shouldBe "Påkrevd request parameter mangler: s"
    }
}