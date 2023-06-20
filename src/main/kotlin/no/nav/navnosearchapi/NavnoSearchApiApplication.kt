package no.nav.navnosearchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [ElasticsearchDataAutoConfiguration::class])
class NavnoSearchApiApplication

fun main(args: Array<String>) {
    runApplication<NavnoSearchApiApplication>(*args)
}
