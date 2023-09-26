package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ARBEIDSGIVER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSEMELDINGER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSE_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRIVATPERSON_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.search.service.search.Filter

val nyheterFilters = mapOf(
    UNDERFASETT_PRIVATPERSON to FilterEntry(
        name = UNDERFASETT_PRIVATPERSON_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.PRIVATPERSON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_ARBEIDSGIVER to FilterEntry(
        name = UNDERFASETT_ARBEIDSGIVER_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.ARBEIDSGIVER.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = UNDERFASETT_STATISTIKK_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = UNDERFASETT_PRESSE_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSE.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSEMELDINGER to FilterEntry(
        name = UNDERFASETT_PRESSEMELDINGER_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSEMELDING.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = UNDERFASETT_NAV_OG_SAMFUNN_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NAV_OG_SAMFUNN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)