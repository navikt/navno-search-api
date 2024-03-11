package no.nav.navnosearchapi.service.search.config

import no.nav.navnosearchadminapi.common.constants.ALL_TEXT
import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes

const val EXACT_PHRASE_MATCH_BOOST = 1.5f
const val FUZZY_LOW_DISTANCE = 6
const val FUZZY_HIGH_DISTANCE = 8
const val NGRAM_MIN_LENGTH = 4

val termsToOverride = mapOf("kontakt" to "kontakt oss")

val fieldsToWeight = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it" to 12.0f,
        "$INGRESS.$it" to 3.0f,
        "$TEXT.$it" to 0.01f,
    )
}.toMap()

val ngramsInnerFieldsToWeight = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it.$NGRAMS_INNER_FIELD" to 11.5f,
        "$INGRESS.$it.$NGRAMS_INNER_FIELD" to 3.0f,
    )
}.toMap()

val exactInnerFieldsToWeight = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it.$EXACT_INNER_FIELD" to 12.0f,
        "$INGRESS.$it.$EXACT_INNER_FIELD" to 6.0f,
        "$TEXT.$it.$EXACT_INNER_FIELD" to 1.0f,
    )
}.toMap()

val typeToWeight = mapOf(
    ValidTypes.OVERSIKT.descriptor to 2.0f,
    ValidTypes.PRODUKTSIDE.descriptor to 2.0f,
    ValidTypes.TEMASIDE.descriptor to 1.75f,
    ValidTypes.SITUASJONSSIDE.descriptor to 1.75f,
    ValidTypes.GUIDE.descriptor to 1.50f,
)

val metatagToWeight = mapOf(
    ValidMetatags.NYHET.descriptor to 0.25f,
)

val allTextFields = languageSubfields.flatMap {
    listOf(
        "$ALL_TEXT.$it",
        "$TITLE.$it.$NGRAMS_INNER_FIELD",
        "$INGRESS.$it.$NGRAMS_INNER_FIELD",
    )
}.toList()
