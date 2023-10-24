package no.nav.navnosearchapi.admin.validation

import no.nav.navnosearchapi.admin.consumer.kodeverk.KodeverkConsumer
import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.enums.DescriptorProvider
import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.METADATA_AUDIENCE
import no.nav.navnosearchapi.common.utils.METADATA_FYLKE
import no.nav.navnosearchapi.common.utils.METADATA_METATAGS
import no.nav.navnosearchapi.common.utils.enumContains
import no.nav.navnosearchapi.common.utils.enumDescriptors
import org.springframework.stereotype.Component

@Component
class ContentDtoValidator(val kodeverkConsumer: KodeverkConsumer) {

    fun validate(content: List<ContentDto>): MutableMap<String, MutableList<String>> {
        val validationErrors = mutableMapOf<String, MutableList<String>>()

        content.forEach {
            val errorMessages = mutableListOf<String>()

            errorMessages.addAll(validateAudience(it.metadata.audience))
            errorMessages.addAll(validateLanguage(it.metadata.language))
            errorMessages.addAll(validateFylke(it.metadata.fylke))
            errorMessages.addAll(validateMetatags(it.metadata.metatags))

            errorMessages.forEach { msg ->
                validationErrors.putIfAbsent(it.id, mutableListOf())
                validationErrors[it.id]!!.add(msg)
            }
        }

        return validationErrors
    }

    private fun validateAudience(audience: List<String>): List<String> {
        return if (audience.isEmpty()) {
            listOf("$METADATA_AUDIENCE må inneholde minst ett element")
        } else audience.mapNotNull { validateValueIsValid<ValidAudiences>(it, METADATA_AUDIENCE) }
    }

    private fun validateLanguage(language: String): List<String> {
        val validLanguages = kodeverkConsumer.fetchSpraakKoder().koder
        return if (!validLanguages.contains(language.uppercase())) {
            listOf("Ugyldig språkkode: $language. Må være tobokstavs språkkode fra kodeverk-api.")
        } else emptyList()
    }

    private fun validateFylke(fylke: String?): List<String> {
        return if (fylke != null) {
            listOfNotNull(validateValueIsValid<ValidFylker>(fylke, METADATA_FYLKE))
        } else emptyList()
    }

    private fun validateMetatags(metatags: List<String>): List<String> {
        return metatags.mapNotNull { validateValueIsValid<ValidMetatags>(it, METADATA_METATAGS) }
    }

    private inline fun <reified T> validateValueIsValid(
        value: String,
        fieldName: String
    ): String? where T : Enum<T>, T : DescriptorProvider {
        return if (!enumContains<T>(value)) {
            "Ugyldig verdi for $fieldName: $value. Gyldige verdier: ${enumDescriptors<T>()}"
        } else null
    }
}