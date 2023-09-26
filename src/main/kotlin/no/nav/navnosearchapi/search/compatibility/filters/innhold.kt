package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.service.search.Filter

val innholdFilters = mapOf(
    UNDERFASETT_INFORMASJON to FilterEntry(
        name = "Informasjon",
        filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.INFORMASJON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_KONTOR to FilterEntry(
        name = "Kontor", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.KONTOR.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
        name = "Søknad og skjema", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.SKJEMA.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)