package no.nav.navnosearchapi.service.mapper

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchapi.client.enums.FieldType
import no.nav.navnosearchapi.service.mapper.extensions.languageSubfieldValue
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Component

//todo: refactor denne klassen

@Component
class HighlightMapper {
    fun toHighlight(searchHit: SearchHit<ContentDao>, isMatchPhraseQuery: Boolean): String {
        val content = searchHit.content
        val defaultFieldType = if (isMatchPhraseQuery) FieldType.EXACT else FieldType.STANDARD

        // todo: Feil konstanter?
        val titleHighlight = searchHit.getHighlightField(TITLE, content.language, defaultFieldType)
        val ingressHighlight = searchHit.getHighlightField(INGRESS, content.language, defaultFieldType)
        val textHighlight = searchHit.getHighlightField(TEXT, content.language, defaultFieldType)

        if (content.type == ValidTypes.TABELL.descriptor) return TABELL

        val hasTitleHighlight = titleHighlight.isNotEmpty()

        fun ingressHighlight() = ingressHighlight.firstOrNull()?.truncateIngress()
        fun textHighlight() = textHighlight.firstOrNull()?.truncateText()

        return ingressHighlight() ?: textHighlight().takeUnless { hasTitleHighlight } ?: content.ingress.languageSubfieldValue(content.language).truncateIngress()
    }

    private fun SearchHit<ContentDao>.getHighlightField(
        baseField: String,
        language: String,
        defaultFieldType: FieldType,
        prioritizedFieldType: FieldType = FieldType.NGRAM
    ): List<String> {
        fun getHighlights(fieldType: FieldType) = getHighlightField(languageSubfieldKey(baseField, language, fieldType))

        return getHighlights(prioritizedFieldType).takeIf { it.isNotEmpty() } ?: getHighlights(defaultFieldType)
    }

    private fun String.truncateText(): String {
        return CUTOFF_PREFIX + take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
    }

    private fun String.truncateIngress(): String {
        return if (length > HIGHLIGHT_MAX_LENGTH) {
            take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
        } else this
    }

    private fun languageSubfieldKey(
        parentKey: String,
        language: String,
        fieldType: FieldType,
    ): String {
        val languageSubfield = parentKey + when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> NORWEGIAN_SUFFIX
            ENGLISH -> ENGLISH_SUFFIX
            else -> OTHER_SUFFIX
        }

        return when (fieldType) {
            FieldType.EXACT -> "$languageSubfield.$EXACT_INNER_FIELD"
            FieldType.NGRAM -> "$languageSubfield.$NGRAMS_INNER_FIELD"
            else -> languageSubfield
        }
    }

    companion object {
        private const val HIGHLIGHT_MAX_LENGTH = 250
        private const val CUTOFF_PREFIX = "… "
        private const val CUTOFF_POSTFIX = " …"
        private const val TABELL = "Tabell"

        private const val NORWEGIAN_SUFFIX = ".no"
        private const val ENGLISH_SUFFIX = ".en"
        private const val OTHER_SUFFIX = ".other"
    }
}