package no.nav.navnosearchapi.validation

import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.utils.dummyContentDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ContentDtoValidatorTest {
    private val validator = ContentDtoValidator()

    @Test
    fun testValidation() {
        val content = listOf(dummyContentDto())
        assertDoesNotThrow { validator.validate(content) }
    }

    @Test
    fun testValidationWithInvalidLanguage() {
        val content = listOf(dummyContentDto().copy(language = "invalid"))
        val exception = assertThrows<ContentValidationException> { validator.validate(content) }
        assertThat(exception.message).isEqualTo("language må være en av følgende gyldige verdier: [no, en, other]")
    }
}