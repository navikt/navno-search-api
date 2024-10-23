package no.nav.navnosearchapi.integrationtests

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import no.nav.navnosearchapi.service.dto.SearchUrlResponse
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

        result.url!! shouldBe EXACT_URL
        result.title!! shouldBe TITLE
    }

    @Test
    fun testSearchWithFuzzyMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUri(FUZZY_URL)).body!!

        result.url!! shouldBe EXACT_URL
        result.title!! shouldBe TITLE
    }

    @Test
    fun testSearchWithNoMatch() {
        val result = restTemplate.getForEntity<SearchUrlResponse>(searchUrlUri(NON_MATCHING_URL)).body!!

        result.url.shouldBeNull()
        result.title.shouldBeNull()
    }
}