package no.nav.navnosearchapi.exception.handler

import no.nav.navnosearchapi.exception.NoIndexForAppException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
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

    @ExceptionHandler(value = [NoIndexForAppException::class])
    fun noSuchIndexHandler(ex: NoIndexForAppException): ResponseEntity<String> {
        val msg = "Fant ingen index for app: ${ex.appName}"
        logger.warn(msg, ex)
        return ResponseEntity.badRequest().body(msg)
    }

    @ExceptionHandler(value = [Throwable::class])
    fun defaultExceptionHandler(ex: Throwable): ResponseEntity<String> {
        val msg = "Ukjent feil"
        logger.error(msg, ex)
        return ResponseEntity.internalServerError().body(msg)
    }
}