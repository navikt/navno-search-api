package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchapi.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.service.search.filter.Filter
import no.nav.navnosearchapi.utils.FYLKE

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
        filterQuery = Filter(
            metatags = listOf(requiredMetatag),
            isFile = listOf(false.toString()),
            requiredMissingFields = listOf(FYLKE)
        ).toQuery()
    )
}