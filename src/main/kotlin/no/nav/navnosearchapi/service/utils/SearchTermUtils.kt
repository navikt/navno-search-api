package no.nav.navnosearchapi.service.utils

private const val QUOTE = '"'

fun String.isInQuotes() = startsWith(QUOTE) && endsWith(QUOTE)