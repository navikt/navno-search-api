package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.search.service.search.Filter

val nyheterFilters = mapOf(
    UNDERFASETT_PRIVATPERSON to FilterEntry(
        name = "Privatperson", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.PRIVATPERSON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_ARBEIDSGIVER to FilterEntry(
        name = "Arbeidsgiver", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.ARBEIDSGIVER.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = "Statistikk", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = "Presse", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSE.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSEMELDINGER to FilterEntry(
        name = "Pressemeldinger", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSEMELDING.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = "NAV og samfunn", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NAV_OG_SAMFUNN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)