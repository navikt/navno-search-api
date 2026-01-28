package no.nav.navnosearchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.opensearch.spring.boot.autoconfigure.OpenSearchRestClientAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@SpringBootApplication(exclude = [OpenSearchRestClientAutoConfiguration::class])
@EnableElasticsearchRepositories(basePackages = ["no.nav.navnosearchadminapi.common"])
class NavnoSearchApiApplication


fun main(args: Array<String>) {
    runApplication<NavnoSearchApiApplication>(*args)
}