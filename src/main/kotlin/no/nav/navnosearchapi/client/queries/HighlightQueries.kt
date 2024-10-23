package no.nav.navnosearchapi.client.queries

import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import no.nav.navnosearchapi.client.enums.FieldType
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.DEFAULT_FRAGMENT_CHAR_SIZE

private const val BOLD_PRETAG = "<b>"
private const val BOLD_POSTTAG = "</b>"

private const val UNFRAGMENTED = 0
private const val SINGLE_FRAGMENT = 1
private const val MAX_FRAGMENT_SIZE = 200

fun highlightBuilder(query: QueryBuilder, isMatchPhraseQuery: Boolean): HighlightBuilder {
    val defaultFieldType = if (isMatchPhraseQuery) FieldType.EXACT else FieldType.STANDARD
    val includeNgrams = !isMatchPhraseQuery

    return HighlightBuilder()
        .highlightQuery(query) // Må bruke query uten function score for å få riktige highlights
        .preTags(BOLD_PRETAG)
        .postTags(BOLD_POSTTAG)
        .highlightFields(TITLE, defaultFieldType)
        .highlightFields(INGRESS, defaultFieldType, includeNgrams)
        .highlightFields(TEXT, defaultFieldType, includeNgrams)
}

private fun HighlightBuilder.highlightFields(
    baseField: String,
    defaultFieldType: FieldType,
    includeNgrams: Boolean = false
): HighlightBuilder {
    languageSubfields.forEach { this.field(baseField, it, defaultFieldType) }

    if (includeNgrams) {
        this.field(baseField, NORWEGIAN, FieldType.NGRAM)
    }

    return this
}

private fun HighlightBuilder.field(
    baseField: String,
    languageSubfield: String,
    fieldType: FieldType,
) {
    val fieldName = "$baseField.$languageSubfield".let {
        when (fieldType) {
            FieldType.EXACT -> "$it.$EXACT_INNER_FIELD"
            FieldType.NGRAM -> "$it.$NGRAMS_INNER_FIELD"
            else -> it
        }
    }

    when (baseField) {
        TITLE, INGRESS -> this.field(fieldName, DEFAULT_FRAGMENT_CHAR_SIZE, UNFRAGMENTED)
        TEXT -> this.field(fieldName, MAX_FRAGMENT_SIZE, SINGLE_FRAGMENT)
    }
}