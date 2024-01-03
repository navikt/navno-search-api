package no.nav.navnosearchapi.integrationtests

import no.nav.navnosearchadminapi.common.repository.ContentRepository
import no.nav.navnosearchapi.utils.initialTestData
import org.junit.jupiter.api.extension.ExtendWith
import org.opensearch.testcontainers.OpensearchContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@Testcontainers(disabledWithoutDocker = true, parallel = true)
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
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
        c: String? = "1",
        start: Int? = 0,
        f: Int? = 0,
        uf: List<String>? = emptyList(),
        s: Int? = 0,
        daterange: Int? = -1,
    ): String {
        return UriComponentsBuilder.fromHttpUrl(host())
            .path("/content/search")
            .queryParam("ord", ord)
            .queryParam("c", c)
            .queryParam("start", start)
            .queryParam("f", f)
            .queryParam("uf", uf)
            .queryParam("s", s)
            .queryParam("daterange", daterange)
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

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of("opensearch.uris=" + opensearch.getHttpHostAddress())
                .applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        @Container
        val opensearch: OpensearchContainer<*> = OpensearchContainer("opensearchproject/opensearch:2.11.1")
            .withStartupAttempts(5)
            .withStartupTimeout(Duration.ofMinutes(2))
    }
}