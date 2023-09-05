package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.validation.enums.ValidLanguages

val NORWEGIAN_BOKMAAL = ValidLanguages.NB.descriptor
val NORWEGIAN_NYNORSK = ValidLanguages.NN.descriptor
val ENGLISH = ValidLanguages.EN.descriptor
val OTHER = ValidLanguages.OTHER.descriptor

const val SEARCH_AS_YOU_TYPE = "searchAsYouType"
const val TITLE = "title"
const val INGRESS = "ingress"
const val TEXT = "text"
const val LAST_UPDATED = "lastUpdated"
const val AUDIENCE = "audience"
const val LANGUAGE = "language"
const val FYLKE = "fylke"
const val METATAGS = "metatags"
const val IS_FILE = "isFile"

const val METADATA_AUDIENCE = "metadata.audience"
const val METADATA_LANGUAGE = "metadata.language"
const val METADATA_FYLKE = "metadata.fylke"
const val METADATA_METATAGS = "metadata.metatags"

const val TITLE_WILDCARD = "$TITLE.*"
const val INGRESS_WILDCARD = "$INGRESS.*"
const val TEXT_WILDCARD = "$TEXT.*"

const val DATE_RANGE_LAST_7_DAYS = "Siste 7 dager"
const val DATE_RANGE_LAST_30_DAYS = "Siste 30 dager"
const val DATE_RANGE_LAST_12_MONTHS = "Siste 12 måneder"
const val DATE_RANGE_OLDER_THAN_12_MONTHS = "Eldre enn 12 måneder"