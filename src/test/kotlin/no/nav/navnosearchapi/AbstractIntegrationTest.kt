package no.nav.navnosearchapi

import no.nav.navnosearchapi.common.repository.ContentRepository
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
import org.springframework.util.MultiValueMapAdapter
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

    fun searchUrl(term: String, page: Int = 0, filters: Map<String, List<String>> = emptyMap()): String {
        return UriComponentsBuilder.fromHttpUrl(host())
            .path("/content/search")
            .queryParam("term", term)
            .queryParam("page", page)
            .queryParams(MultiValueMapAdapter(filters))
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
        val opensearch: OpensearchContainer = OpensearchContainer("opensearchproject/opensearch:2.0.0")
            .withStartupAttempts(5)
            .withStartupTimeout(Duration.ofMinutes(2))
    }
}