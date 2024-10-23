package no.nav.navnosearchapi.handler

import java.time.ZonedDateTime

data class ErrorResponse(
    val timestamp: ZonedDateTime,
    val status: Int,
    val error: String,
    val message: String?,
    val path: String
)