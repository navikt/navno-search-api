package no.nav.navnosearchapi.service.utils

private const val QUOTE = '"'

fun isInQuotes(term: String): Boolean {
    return term.startsWith(QUOTE) && term.endsWith(QUOTE)
}
