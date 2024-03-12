package no.nav.navnosearchapi.integrationtests

import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.repository.ContentRepository
import no.nav.navnosearchapi.integrationtests.config.OpensearchConfiguration
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.utils.initialTestData
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@Import(OpensearchConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var repository: ContentRepository

    @LocalServerPort
    var serverPort: Int? = null

    fun host() = "http://localhost:$serverPort"

    fun searchUri(
        ord: String?,
        page: Int? = 0,
        f: String? = FacetKeys.PRIVATPERSON,
        uf: List<String>? = emptyList(),
        s: Int? = 0,
        preferredLanguage: String = NORWEGIAN_BOKMAAL
    ): String {
        return UriComponentsBuilder.fromHttpUrl(host())
            .path("/content/search")
            .queryParam("ord", ord)
            .queryParam("page", page)
            .queryParam("f", f)
            .queryParam("uf", uf)
            .queryParam("s", s)
            .build().toUriString()
    }

    fun searchUrlUri(term: String): String {
        return UriComponentsBuilder.fromHttpUrl(host())
            .path("/content/search-url")
            .queryParam("term", term)
            .build().toUriString()
    }

    fun setupIndex() {
        repository.deleteAll()
        repository.saveAll(initialTestData)
    }
}