package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.search.service.search.Filter

val innholdFilters = mapOf(
    UNDERFASETT_INFORMASJON to FilterEntry(
        name = UNDERFASETT_INFORMASJON_NAME,
        filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.INFORMASJON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_KONTOR to FilterEntry(
        name = UNDERFASETT_KONTOR_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.KONTOR.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
        name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.SKJEMA.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)