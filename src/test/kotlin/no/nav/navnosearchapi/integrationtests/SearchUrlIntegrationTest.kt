package no.nav.navnosearchapi.integrationtests

import no.nav.navnosearchapi.service.dto.SearchUrlResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity

class SearchUrlIntegrationTest : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        setupIndex()
    }

    val EXACT_URL = "https://First.com"
    val FUZZY_URL = "https://Fristt.com"
    val NON_MATCHING_URL = "1337"
    val TITLE = "First title"

    @Test
    fun testSearchWithExactMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUri(EXACT_URL)).body!!

        assertThat(result.url)!!.isEqualTo(EXACT_URL)
        assertThat(result.title)!!.isEqualTo(TITLE)
    }

    @Test
    fun testSearchWithFuzzyMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUri(FUZZY_URL)).body!!

        assertThat(result.url)!!.isEqualTo(EXACT_URL)
        assertThat(result.title)!!.isEqualTo(TITLE)
    }

    @Test
    fun testSearchWithNoMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUri(NON_MATCHING_URL)).body!!

        assertThat(result.url).isNull()
        assertThat(result.title).isNull()
    }
}