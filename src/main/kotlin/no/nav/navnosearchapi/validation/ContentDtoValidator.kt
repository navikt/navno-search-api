package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.NORWEGIAN
import org.springframework.stereotype.Component

@Component
class ContentDtoValidator {
    fun validate(content: List<ContentDto>) {
        content.forEach {
            if (!VALID_LANGS.contains(it.language)) {
                throw ContentValidationException("language må være en av følgende gyldige verdier: $VALID_LANGS")
            }
        }
    }

    companion object {
        private val VALID_LANGS = listOf(ENGLISH, NORWEGIAN)
    }
}