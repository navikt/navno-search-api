package no.nav.navnosearchapi.integrationtests

import no.nav.navnosearchapi.service.search.dto.SearchUrlResponse
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

    @Test
    fun testSearchWithExactMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUrl(EXACT_URL)).body!!

        assertThat(result.suggestion)!!.isEqualTo(EXACT_URL)
    }

    @Test
    fun testSearchWithFuzzyMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUrl(FUZZY_URL)).body!!

        assertThat(result.suggestion)!!.isEqualTo(EXACT_URL)
    }

    @Test
    fun testSearchWithNoMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUrl(NON_MATCHING_URL)).body!!

        assertThat(result.suggestion).isNull()
    }
}