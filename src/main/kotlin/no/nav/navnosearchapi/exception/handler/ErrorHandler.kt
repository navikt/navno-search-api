package no.nav.navnosearchapi.exception.handler

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import no.nav.navnosearchapi.exception.ContentValidationException
import no.nav.navnosearchapi.exception.DocumentForTeamNameNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ErrorHandler {

    val logger: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    fun missingRequestParamHandler(ex: MissingServletRequestParameterException): ResponseEntity<String> {
        val msg = "Påkrevd request parameter mangler: ${ex.parameterName}"
        logger.warn(msg, ex)
        return ResponseEntity.badRequest().body(msg)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun missingRequiredFieldHandler(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        if (ex.cause is MissingKotlinParameterException) {
            val msg = "Påkrevd felt mangler: ${(ex.cause as MissingKotlinParameterException).parameter.name}"
            logger.warn(msg, ex)
            return ResponseEntity.badRequest().body(msg)
        }
        return defaultExceptionHandler(ex)
    }

    @ExceptionHandler(value = [ContentValidationException::class])
    fun contentValidationExceptionHandler(ex: ContentValidationException): ResponseEntity<String> {
        val msg = "Validering feilet: ${ex.message}"
        logger.warn(msg, ex)
        return ResponseEntity.badRequest().body(msg)
    }

    @ExceptionHandler(value = [DocumentForTeamNameNotFoundException::class])
    fun documentForTeamNameNotFoundHandler(ex: DocumentForTeamNameNotFoundException): ResponseEntity<String> {
        logger.warn(ex.message, ex)
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(value = [Throwable::class])
    fun defaultExceptionHandler(ex: Throwable): ResponseEntity<String> {
        val msg = "Ukjent feil"
        logger.error(msg, ex)
        return ResponseEntity.internalServerError().body(msg)
    }
}