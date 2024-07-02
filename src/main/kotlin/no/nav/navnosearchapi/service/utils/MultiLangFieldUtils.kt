package no.nav.navnosearchapi.service.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.model.MultiLangField
import no.nav.navnosearchapi.client.enums.FieldType

private const val NORWEGIAN_SUFFIX = ".no"
private const val ENGLISH_SUFFIX = ".en"
private const val OTHER_SUFFIX = ".other"
private const val EXACT_INNER_FIELD_SUFFIX = ".$EXACT_INNER_FIELD"
private const val NGRAMS_INNER_FIELD_SUFFIX = ".$NGRAMS_INNER_FIELD"

fun languageSubfieldKey(
    parentKey: String,
    language: String,
    fieldType: FieldType,
): String {
    return buildString {
        append(parentKey)
        append(
            when (language) {
                NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> NORWEGIAN_SUFFIX
                ENGLISH -> ENGLISH_SUFFIX
                else -> OTHER_SUFFIX
            }
        )
        if (fieldType == FieldType.EXACT) append(EXACT_INNER_FIELD_SUFFIX)
        else if (fieldType == FieldType.NGRAM) append(NGRAMS_INNER_FIELD_SUFFIX)
    }
}

fun MultiLangField.languageSubfieldValue(language: String): String {
    return when (language) {
        NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> no
        ENGLISH -> en
        else -> other
    } ?: ""
}

fun MultiLangField.value() = listOf(no, en, other).firstOrNull()