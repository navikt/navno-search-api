package no.nav.navnosearchapi

import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.utils.indexCoordinates
import no.nav.navnosearchapi.utils.indexName
import no.nav.navnosearchapi.utils.initialTestData
import org.opensearch.testcontainers.OpensearchContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.index.AliasAction
import org.springframework.data.elasticsearch.core.index.AliasActionParameters
import org.springframework.data.elasticsearch.core.index.AliasActions
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@Testcontainers(disabledWithoutDocker = true, parallel = true)
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var operations: ElasticsearchOperations

    @LocalServerPort
    var serverPort: Int? = null

    final val appName = "testapp"
    final val aliasName = "search-content"
    final val indexName = indexName(appName)
    final val indexCoordinates = indexCoordinates(appName)

    fun host() = "http://localhost:$serverPort"

    fun indexCount() = operations.count(operations.matchAllQuery(), indexCoordinates)

    fun searchUrl(term: String, page: Int = 0, maalgruppe: String? = null): String {
        val maalgruppeParam = maalgruppe?.let { "&maalgruppe=$it" } ?: ""
        return "${host()}/content/search?page=$page&term=$term$maalgruppeParam"
    }

    fun setupIndex() {
        operations.indexOps(indexCoordinates).delete()
        operations.indexOps(indexCoordinates).create()
        operations.indexOps(indexCoordinates).putMapping(ContentDao::class.java)
        operations.indexOps(indexCoordinates).alias(aliasActions())
        operations.save(initialTestData, indexCoordinates)
        operations.indexOps(indexCoordinates).refresh()
    }

    private fun aliasActions() = AliasActions(
        AliasAction.Add(
            AliasActionParameters.builder()
                .withIndices(indexName)
                .withAliases(aliasName)
                .build()
        )
    )

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