package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.search.enums.FieldType
import no.nav.navnosearchapi.search.utils.languageSubfieldKey
import no.nav.navnosearchapi.search.utils.languageSubfieldValue
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Component

@Component
class HighlightMapper {
    fun toHighlight(searchHit: SearchHit<Content>, isMatchPhraseQuery: Boolean): String {
        return if (searchHit.content.type == ValidTypes.TABELL.descriptor) {
            TABELL
        } else {
            highlightValues(searchHit, isMatchPhraseQuery).let { (titleHighlight, ingressHighlight, textHighlight) ->
                with(searchHit.content) {
                    when {
                        ingressHighlight != null -> ingressHighlight.truncateIngress()
                        textHighlight != null && titleHighlight == null -> textHighlight.truncateText()
                        else -> ingress.languageSubfieldValue(language).truncateIngress()
                    }
                }
            }
        }
    }

    fun highlightValues(
        searchHit: SearchHit<Content>,
        isMatchPhraseQuery: Boolean
    ): Triple<String?, String?, String?> {
        val defaultFieldType = if (isMatchPhraseQuery) FieldType.EXACT else FieldType.STANDARD

        val language = searchHit.content.language

        return Triple(
            searchHit.getHighlightField(TITLE, language, defaultFieldType),
            searchHit.getHighlightField(INGRESS, language, defaultFieldType),
            searchHit.getHighlightField(TEXT, language, defaultFieldType)
        )
    }

    private fun SearchHit<Content>.getHighlightField(
        baseField: String,
        language: String,
        defaultFieldType: FieldType,
        prioritizedFieldType: FieldType = FieldType.NGRAM
    ): String? {
        fun getHighlight(fieldType: FieldType): String? {
            return getHighlightField(languageSubfieldKey(baseField, language, fieldType)).firstOrNull()
        }

        return getHighlight(prioritizedFieldType) ?: getHighlight(defaultFieldType)
    }

    private fun String.truncateText(): String {
        return CUTOFF_PREFIX + take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
    }

    private fun String.truncateIngress(): String {
        return if (length > HIGHLIGHT_MAX_LENGTH) {
            take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
        } else this
    }

    companion object {
        private const val HIGHLIGHT_MAX_LENGTH = 250
        private const val CUTOFF_PREFIX = "… "
        private const val CUTOFF_POSTFIX = " …"
        private const val TABELL = "Tabell"
    }
}