package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.utils.METADATA_AUDIENCE
import no.nav.navnosearchapi.utils.METADATA_FYLKE
import no.nav.navnosearchapi.utils.METADATA_LANGUAGE
import no.nav.navnosearchapi.utils.METADATA_METATAGS
import no.nav.navnosearchapi.utils.enumContains
import no.nav.navnosearchapi.utils.enumDescriptors
import no.nav.navnosearchapi.validation.enums.DescriptorProvider
import no.nav.navnosearchapi.validation.enums.ValidAudiences
import no.nav.navnosearchapi.validation.enums.ValidFylker
import no.nav.navnosearchapi.validation.enums.ValidLanguages
import no.nav.navnosearchapi.validation.enums.ValidMetatags
import org.springframework.stereotype.Component

@Component
class ContentDtoValidator {
    fun validate(content: List<ContentDto>) {
        content.forEach {
            validateAudience(it.metadata.audience)
            validateLanguage(it.metadata.language)
            it.metadata.fylke?.let { fylke -> validateFylke(fylke) }
            it.metadata.metatags?.let { metatags -> validateMetatags(metatags) }
        }
    }

    private fun validateAudience(audience: List<String>) {
        validateNotEmpty(audience, METADATA_AUDIENCE)
        audience.forEach { validateValueIsValid<ValidAudiences>(it, METADATA_AUDIENCE) }
    }

    private fun validateLanguage(language: String) {
        validateValueIsValid<ValidLanguages>(language, METADATA_LANGUAGE)
    }

    private fun validateFylke(fylke: String) {
        validateValueIsValid<ValidFylker>(fylke, METADATA_FYLKE)
    }

    private fun validateMetatags(metatags: List<String>) {
        metatags.forEach { validateValueIsValid<ValidMetatags>(it, METADATA_METATAGS) }
    }

    private fun validateNotEmpty(values: List<String>, fieldName: String) {
        if (values.isEmpty()) {
            throw ContentValidationException("$fieldName må inneholde minst ett element")
        }
    }

    private inline fun <reified T> validateValueIsValid(
        value: String,
        fieldName: String
    ) where T : Enum<T>, T : DescriptorProvider {
        if (!enumContains<T>(value)) {
            throw ContentValidationException("Ugyldig verdi for $fieldName: $value. Gyldige verdier: ${enumDescriptors<T>()}")
        }
    }
}