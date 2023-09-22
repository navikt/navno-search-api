package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.admin.consumer.kodeverk.KodeverkConsumer
import no.nav.navnosearchapi.admin.validation.ContentDtoValidator
import no.nav.navnosearchapi.common.exception.ContentValidationException
import no.nav.navnosearchapi.utils.dummyContentDto
import no.nav.navnosearchapi.utils.mockedKodeverkResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ContentDtoValidatorTest(@Mock val kodeverkConsumer: KodeverkConsumer) {

    private val invalidValue = "invalidValue"

    private val validator = ContentDtoValidator(kodeverkConsumer)

    @BeforeEach
    fun setup() {
        Mockito.`when`(kodeverkConsumer.fetchSpraakKoder()).thenReturn(mockedKodeverkResponse)
    }

    @Test
    fun testValidation() {
        val content = listOf(dummyContentDto())
        assertDoesNotThrow { validator.validate(content) }
    }

    @Test
    fun testValidationWithMissingAudience() {
        val content = listOf(dummyContentDto(audience = emptyList()))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("metadata.audience må inneholde minst ett element")
    }

    @Test
    fun testValidationWithInvalidAudience() {
        val content = listOf(dummyContentDto(audience = listOf(invalidValue)))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for metadata.audience: $invalidValue. Gyldige verdier: [privatperson, arbeidsgiver, samarbeidspartner, andre]")
    }

    @Test
    fun testValidationWithInvalidFylke() {
        val content = listOf(dummyContentDto(fylke = invalidValue))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for metadata.fylke: $invalidValue. Gyldige verdier: [agder, innlandet, more-og-romsdal, nordland, oslo, rogaland, troms-og-finnmark, trondelag, vestfold-og-telemark, vestland, vest-viken, ost-viken]")
    }

    @Test
    fun testValidationWithInvalidMetatag() {
        val content = listOf(dummyContentDto(metatags = listOf(invalidValue)))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig verdi for metadata.metatags: $invalidValue. Gyldige verdier: [informasjon, kontor, skjema, nyhet, presse, pressemelding, nav-og-samfunn, analyse, statistikk]")
    }

    @Test
    fun testValidationWithInvalidLanguage() {
        val content = listOf(dummyContentDto(language = invalidValue))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("Ugyldig språkkode: invalidValue. Må være tobokstavs språkkode fra kodeverk-api.")
    }
}