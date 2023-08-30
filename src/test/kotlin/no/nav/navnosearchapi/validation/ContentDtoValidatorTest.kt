package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.utils.dummyContentDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ContentDtoValidatorTest {
    private val validator = ContentDtoValidator()
    private val invalidValue = "invalidValue"

    @Test
    fun testValidation() {
        val content = listOf(dummyContentDto())
        assertDoesNotThrow { validator.validate(content) }
    }

    @Test
    fun testValidationWithMissingAudience() {
        val content = listOf(dummyContentDto().copy(audience = emptyList()))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("audience må inneholde minst ett element")
    }

    @Test
    fun testValidationWithInvalidAudience() {
        val content = listOf(dummyContentDto().copy(audience = listOf(invalidValue)))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for audience: $invalidValue. Gyldige verdier: [privatperson, arbeidsgiver, samarbeidspartner, andre]")
    }

    @Test
    fun testValidationWithInvalidLanguage() {
        val content = listOf(dummyContentDto().copy(language = invalidValue))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for language: $invalidValue. Gyldige verdier: [no, en, other]")
    }

    @Test
    fun testValidationWithInvalidFylke() {
        val content = listOf(dummyContentDto().copy(fylke = invalidValue))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for fylke: $invalidValue. Gyldige verdier: [agder, innlandet, more-og-romsdal, nordland, oslo, rogaland, troms-og-finnmark, trondelag, vestfold-og-telemark, vestland, vest-viken, ost-viken]")
    }

    @Test
    fun testValidationWithInvalidMetatag() {
        val content = listOf(dummyContentDto().copy(metatags = listOf(invalidValue)))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for metatags: $invalidValue. Gyldige verdier: [kontor, skjema, nyhet, pressemelding, nav-og-samfunn, analyse, statistikk]")
    }
}