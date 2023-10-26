package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.search.search.filter.Filter

val innholdFilters = mapOf(
    UNDERFASETT_INFORMASJON to innholdFilter(
        name = UNDERFASETT_INFORMASJON_NAME,
        requiredMetatag = ValidMetatags.INFORMASJON.descriptor
    ),
    UNDERFASETT_KONTOR to innholdFilter(
        name = UNDERFASETT_KONTOR_NAME,
        requiredMetatag = ValidMetatags.KONTOR.descriptor
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to innholdFilter(
        name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME,
        requiredMetatag = ValidMetatags.SKJEMA.descriptor
    ),
)

private fun innholdFilter(name: String, requiredMetatag: String): FilterEntry {
    return FilterEntry(
        name = name,
        filters = listOf(
            Filter(
                metatags = listOf(requiredMetatag),
                isFile = listOf(false.toString()),
                requiredMissingFields = listOf(FYLKE)
            ).toQuery()
        )
    )
}