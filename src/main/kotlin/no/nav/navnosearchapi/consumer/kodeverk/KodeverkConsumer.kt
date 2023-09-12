package no.nav.navnosearchapi.consumer.kodeverk

import no.nav.navnosearchapi.consumer.kodeverk.dto.KodeverkResponse
import no.nav.navnosearchapi.exception.KodeverkConsumerException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class KodeverkConsumer(val restTemplate: RestTemplate, @Value("\${kodeverk.spraak.url}") val kodeverkUrl: String) {
    fun fetchSpraakKoder(): KodeverkResponse {
        try {
            val response = restTemplate.exchange(
                kodeverkUrl,
                HttpMethod.GET,
                HttpEntity<Any>(headers()),
                KodeverkResponse::class.java
            )
            return response.body ?: throw KodeverkConsumerException("Tom response body fra kodeverk-api")
        } catch (e: HttpStatusCodeException) {
            throw KodeverkConsumerException("Feil ved kall til kodeverk-api. ${e.message}", e)
        }
    }

    private fun headers(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set(NAV_CALL_ID, UUID.randomUUID().toString())
        headers.set(NAV_CONSUMER_ID, NAVNO_SEARCH_API)
        return headers
    }

    companion object {
        const val NAV_CALL_ID = "Nav-Call-Id"
        const val NAV_CONSUMER_ID = "Nav-Consumer-Id"

        const val NAVNO_SEARCH_API = "navno-search-api"
    }
}