package no.nav.navnosearchapi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.utils.indexCoordinates
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.opensearch.testcontainers.OpensearchContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitsImpl
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration


@OptIn(ExperimentalCoroutinesApi::class)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = [ContentRepositoryIntegrationTests.Initializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContentRepositoryIntegrationTests(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val operations: ElasticsearchOperations,
    @LocalServerPort val serverPort: Int,
) {
    @Test
    fun testContent() = runTest {
        val content = Content("id", "https://href.com", "name", "ingress", "text", "Privatperson")

        val indexCoordinates = indexCoordinates("testapp")

        runBlocking {
            operations.indexOps(indexCoordinates).create()
            operations.save(content, indexCoordinates)
        }

        val result: ResponseEntity<SearchHitsImpl<Content>> = runBlocking {
            restTemplate.getForEntity("http://localhost:$serverPort/content/testapp")
        }

        assertThat(result.body?.totalHits).isEqualTo(1L)
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