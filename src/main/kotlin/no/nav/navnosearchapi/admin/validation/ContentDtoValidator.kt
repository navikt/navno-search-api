package no.nav.navnosearchapi.admin.validation

import no.nav.navnosearchapi.admin.consumer.kodeverk.KodeverkConsumer
import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.enums.DescriptorProvider
import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.exception.ContentValidationException
import no.nav.navnosearchapi.common.utils.METADATA_AUDIENCE
import no.nav.navnosearchapi.common.utils.METADATA_FYLKE
import no.nav.navnosearchapi.common.utils.METADATA_METATAGS
import no.nav.navnosearchapi.common.utils.enumContains
import no.nav.navnosearchapi.common.utils.enumDescriptors
import org.springframework.stereotype.Component

@Component
class ContentDtoValidator(val kodeverkConsumer: KodeverkConsumer) {
    fun validate(content: List<ContentDto>) {
        content.forEach {
            validateAudience(it.metadata.audience)
            validateLanguage(it.metadata.language)
            it.metadata.fylke?.let { fylke -> validateFylke(fylke) }
            it.metadata.metatags.let { metatags -> validateMetatags(metatags) }
        }
    }

    private fun validateAudience(audience: List<String>) {
        validateNotEmpty(audience, METADATA_AUDIENCE)
        audience.forEach { validateValueIsValid<ValidAudiences>(it, METADATA_AUDIENCE) }
    }

    private fun validateLanguage(language: String) {
        val validLanguages = kodeverkConsumer.fetchSpraakKoder().koder
        if (!validLanguages.contains(language.uppercase())) {
            throw ContentValidationException("Ugyldig språkkode: $language. Må være tobokstavs språkkode fra kodeverk-api.")
        }
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