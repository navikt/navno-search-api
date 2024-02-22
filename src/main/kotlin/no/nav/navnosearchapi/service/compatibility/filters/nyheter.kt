package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK_AGGREGATION_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val nyheterFilters = mapOf(
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = UNDERFASETT_STATISTIKK_NAME,
        aggregationName = UNDERFASETT_STATISTIKK_AGGREGATION_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor)),
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = UNDERFASETT_PRESSE_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.PRESSE.descriptor)),
    ),
    UNDERFASETT_PRESSEMELDINGER to FilterEntry(
        name = UNDERFASETT_PRESSEMELDINGER_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.PRESSEMELDING.descriptor)),
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = UNDERFASETT_NAV_OG_SAMFUNN_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.NAV_OG_SAMFUNN.descriptor)),
    ),
)

private fun nyhetFilter(requiredMetatags: List<String>): BoolQueryBuilder {
    val query = BoolQueryBuilder().mustNot(existsQuery(FYLKE))

    requiredMetatags.forEach { query.must(termQuery(METATAGS, it)) }

    return query
}