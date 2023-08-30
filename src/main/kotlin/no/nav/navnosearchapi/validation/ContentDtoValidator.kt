package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.utils.enumContains
import no.nav.navnosearchapi.utils.enumDescriptors
import org.springframework.stereotype.Component

@Component
class ContentDtoValidator {
    fun validate(content: List<ContentDto>) {
        content.forEach {
            if (!enumContains<ValidLanguages>(it.language)) {
                throw ContentValidationException("language må være en av følgende gyldige verdier: ${enumDescriptors<ValidLanguages>()}")
            }
        }
    }
}