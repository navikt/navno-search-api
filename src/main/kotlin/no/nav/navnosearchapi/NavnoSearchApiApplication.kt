package no.nav.navnosearchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchAutoConfiguration
import org.springframework.boot.data.elasticsearch.autoconfigure.DataElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchClientAutoConfiguration
import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchRestClientAutoConfiguration
import org.springframework.boot.elasticsearch.autoconfigure.health.ElasticsearchRestHealthContributorAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        DataElasticsearchAutoConfiguration::class,
        DataElasticsearchRepositoriesAutoConfiguration::class,
        ElasticsearchClientAutoConfiguration::class,
        ElasticsearchRestClientAutoConfiguration::class,
        ElasticsearchRestHealthContributorAutoConfiguration::class,
    ]
)
class NavnoSearchApiApplication

fun main(args: Array<String>) {
    runApplication<NavnoSearchApiApplication>(*args)
}