package no.nav.navnosearchapi.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.time.Clock

@Configuration
@EnableElasticsearchRepositories(basePackages = ["no.nav.navnosearchadminapi.common"])
class ApplicationConfig {
    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}