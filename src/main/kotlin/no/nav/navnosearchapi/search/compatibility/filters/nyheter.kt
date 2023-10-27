package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.FYLKE
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
import no.nav.navnosearchapi.search.search.filter.Filter

val nyheterFilters = mapOf(
    UNDERFASETT_PRIVATPERSON to nyhetFilter(
        name = UNDERFASETT_PRIVATPERSON_NAME,
        requiredMetatags = listOf(ValidMetatags.NYHET.descriptor),
        requiredAudience = ValidAudiences.PRIVATPERSON.descriptor
    ),
    UNDERFASETT_ARBEIDSGIVER to nyhetFilter(
        name = UNDERFASETT_ARBEIDSGIVER_NAME,
        requiredMetatags = listOf(ValidMetatags.NYHET.descriptor),
        requiredAudience = ValidAudiences.ARBEIDSGIVER.descriptor
    ),
    UNDERFASETT_STATISTIKK to nyhetFilter(
        name = UNDERFASETT_STATISTIKK_NAME,
        requiredMetatags = listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor)
    ),
    UNDERFASETT_PRESSE to nyhetFilter(
        name = UNDERFASETT_PRESSE_NAME,
        requiredMetatags = listOf(ValidMetatags.PRESSE.descriptor)
    ),
    UNDERFASETT_PRESSEMELDINGER to nyhetFilter(
        name = UNDERFASETT_PRESSEMELDINGER_NAME,
        requiredMetatags = listOf(ValidMetatags.PRESSEMELDING.descriptor)
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to nyhetFilter(
        name = UNDERFASETT_NAV_OG_SAMFUNN_NAME,
        requiredMetatags = listOf(ValidMetatags.NAV_OG_SAMFUNN.descriptor)
    ),
)

private fun nyhetFilter(
    name: String,
    requiredMetatags: List<String>,
    requiredAudience: String? = null
): FilterEntry {
    return FilterEntry(
        name = name,
        filterQuery = Filter(
            metatags = requiredMetatags,
            audience = listOfNotNull(requiredAudience),
            isFile = listOf(false.toString()),
            requiredMissingFields = listOf(FYLKE)
        ).toQuery()
    )
}