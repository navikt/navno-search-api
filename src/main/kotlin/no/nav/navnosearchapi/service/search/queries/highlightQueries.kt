package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.INGRESS_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TEXT_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TITLE_WILDCARD
import no.nav.navnosearchadminapi.common.model.ContentDao
import org.springframework.data.elasticsearch.core.query.HighlightQuery
import org.springframework.data.elasticsearch.core.query.highlight.Highlight
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters.HighlightFieldParametersBuilder

private const val BOLD_PRETAG = "<b>"
private const val BOLD_POSTTAG = "</b>"

private const val UNFRAGMENTED = 0
private const val MAX_FRAGMENT_SIZE = 200

private const val EMPTY_STRING = ""

fun highlightQuery(isMatchPhraseQuery: Boolean = false): HighlightQuery {
    val suffix = if (isMatchPhraseQuery) EXACT_INNER_FIELD_PATH else EMPTY_STRING
    return HighlightQuery(
        Highlight(
            listOf(
                highlightField(fieldName = TITLE_WILDCARD + suffix, isFragmented = false),
                highlightField(fieldName = INGRESS_WILDCARD + suffix, isFragmented = false),
                highlightField(fieldName = TEXT_WILDCARD + suffix, isFragmented = true),
            )
        ),
        ContentDao::class.java
    )
}

private fun highlightField(fieldName: String, isFragmented: Boolean): HighlightField {
    val highlightFieldParametersBuilder = HighlightFieldParametersBuilder()
        .withPreTags(BOLD_PRETAG)
        .withPostTags(BOLD_POSTTAG)

    if (isFragmented) {
        highlightFieldParametersBuilder.withFragmentSize(MAX_FRAGMENT_SIZE)
    } else {
        highlightFieldParametersBuilder.withNumberOfFragments(UNFRAGMENTED)
    }

    return HighlightField(
        fieldName,
        highlightFieldParametersBuilder.build()
    )
}