package no.nav.navnosearchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    excludeName = [
        "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration",
        "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration",
        "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration",
    ],
)
class NavnoSearchApiApplication

fun main(args: Array<String>) {
    runApplication<NavnoSearchApiApplication>(*args)
}