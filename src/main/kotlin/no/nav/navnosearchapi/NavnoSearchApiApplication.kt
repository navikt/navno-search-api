package no.nav.navnosearchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication(exclude = [ElasticsearchDataAutoConfiguration::class])
@EnableCaching
class NavnoSearchApiApplication

fun main(args: Array<String>) {
    runApplication<NavnoSearchApiApplication>(*args)
}
