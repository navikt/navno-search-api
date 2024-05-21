package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import no.nav.navnosearchapi.service.search.enums.FieldType
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.DEFAULT_FRAGMENT_CHAR_SIZE

private const val BOLD_PRETAG = "<b>"
private const val BOLD_POSTTAG = "</b>"

private const val UNFRAGMENTED = 0
private const val SINGLE_FRAGMENT = 1
private const val MAX_FRAGMENT_SIZE = 200

fun highlightBuilder(query: QueryBuilder, isMatchPhraseQuery: Boolean): HighlightBuilder {
    return HighlightBuilder()
        .highlightQuery(query) // Må bruke query uten function score for å få riktige highlights
        .preTags(BOLD_PRETAG)
        .postTags(BOLD_POSTTAG)
        .titleHighlightFields(isMatchPhraseQuery)
        .ingressHighlightFields(isMatchPhraseQuery)
        .textHighlightFields(isMatchPhraseQuery)
}

private fun HighlightBuilder.titleHighlightFields(isMatchPhraseQuery: Boolean): HighlightBuilder {
    if (isMatchPhraseQuery) {
        this.languageSubfields(TITLE, FieldType.EXACT)
    } else {
        this.languageSubfields(TITLE, FieldType.STANDARD)
        this.field(TITLE, NORWEGIAN, FieldType.NGRAM)
    }

    return this
}

private fun HighlightBuilder.ingressHighlightFields(isMatchPhraseQuery: Boolean): HighlightBuilder {
    if (isMatchPhraseQuery) {
        this.languageSubfields(INGRESS, FieldType.EXACT)
    } else {
        this.languageSubfields(INGRESS, FieldType.STANDARD)
        this.field(INGRESS, NORWEGIAN, FieldType.NGRAM)
    }

    return this
}

private fun HighlightBuilder.textHighlightFields(isMatchPhraseQuery: Boolean): HighlightBuilder {
    if (isMatchPhraseQuery) {
        this.languageSubfields(TEXT, FieldType.EXACT)
    } else {
        this.languageSubfields(TEXT, FieldType.STANDARD)
    }

    return this
}

private fun HighlightBuilder.languageSubfields(baseField: String, fieldType: FieldType) {
    languageSubfields.forEach { this.field(baseField, it, fieldType) }
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