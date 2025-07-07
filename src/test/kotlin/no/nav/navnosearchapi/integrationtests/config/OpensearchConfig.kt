package no.nav.navnosearchapi.integrationtests.config

import org.opensearch.testcontainers.OpenSearchContainer
import org.springframework.boot.test.context.TestConfiguration
import java.time.Duration

@TestConfiguration
class OpensearchConfig {
    companion object {
        private val opensearch: OpenSearchContainer<*> = OpenSearchContainer("opensearchproject/opensearch:3.0.1")
            .withStartupAttempts(5)
            .withStartupTimeout(Duration.ofMinutes(2))

        init {
            System.setProperty("opensearch.uris", opensearch.httpHostAddress)
        }
    }
}