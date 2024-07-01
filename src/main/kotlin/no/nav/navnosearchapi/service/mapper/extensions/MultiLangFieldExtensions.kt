package no.nav.navnosearchapi.service.mapper.extensions

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.model.MultiLangField

fun MultiLangField.languageSubfieldValue(language: String): String {
    return when (language) {
        NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> no
        ENGLISH -> en
        else -> other
    } ?: ""
}

fun MultiLangField.value() = listOf(no, en, other).firstOrNull()