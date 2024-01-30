package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.DEFAULT_FRAGMENT_CHAR_SIZE
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.DEFAULT_NUMBER_OF_FRAGMENTS

private const val BOLD_PRETAG = "<b>"
private const val BOLD_POSTTAG = "</b>"

private const val UNFRAGMENTED = 0
private const val MAX_FRAGMENT_SIZE = 200

fun highlightBuilder(query: QueryBuilder, isMatchPhraseQuery: Boolean): HighlightBuilder {
    return HighlightBuilder()
        .highlightQuery(query) // Må bruke query uten function score for å få riktige highlights
        .preTags(BOLD_PRETAG)
        .postTags(BOLD_POSTTAG)
        .requireFieldMatch(false) // For å highlighte text-felt selv om dette ikke er med i søkequery
        .ingressHighlightFields(isMatchPhraseQuery)
        .textHighlightFields(isMatchPhraseQuery)
}

private fun HighlightBuilder.ingressHighlightFields(isMatchPhraseQuery: Boolean): HighlightBuilder {
    languageSubfields.forEach {
        val fieldName = highlightFieldName(INGRESS, it, isMatchPhraseQuery)
        this.field(fieldName, DEFAULT_FRAGMENT_CHAR_SIZE, UNFRAGMENTED)
    }
    return this
}

private fun HighlightBuilder.textHighlightFields(isMatchPhraseQuery: Boolean): HighlightBuilder {
    languageSubfields.forEach {
        val fieldName = highlightFieldName(TEXT, it, isMatchPhraseQuery)
        this.field(fieldName, MAX_FRAGMENT_SIZE, DEFAULT_NUMBER_OF_FRAGMENTS)
    }
    return this
}

private fun highlightFieldName(baseField: String, languageSubfield: String, isMatchPhraseQuery: Boolean): String {
    val fieldName = "$baseField.$languageSubfield"

    return if (isMatchPhraseQuery) {
        "$fieldName.$EXACT_INNER_FIELD"
    } else {
        fieldName
    }
}