package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.NYHETER_STATISTIKK_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val nyheterFilters = mapOf(
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = UNDERFASETT_STATISTIKK_NAME,
        aggregationName = NYHETER_STATISTIKK_AGG_NAME,
        filterQuery = nyhetFilter(
            mustHaveMetatags = listOf(
                ValidMetatags.NYHET.descriptor,
                ValidMetatags.STATISTIKK.descriptor
            )
        ),
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = UNDERFASETT_PRESSE_NAME,
        filterQuery = nyhetFilter(
            shouldHaveMetatags = listOf(
                ValidMetatags.PRESSE.descriptor,
                ValidMetatags.PRESSEMELDING.descriptor
            )
        ),
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = UNDERFASETT_NAV_OG_SAMFUNN_NAME,
        filterQuery = nyhetFilter(
            mustHaveMetatags = listOf(
                ValidMetatags.NYHET.descriptor,
                ValidMetatags.NAV_OG_SAMFUNN.descriptor
            )
        ),
    ),
)

private fun nyhetFilter(
    shouldHaveMetatags: List<String> = emptyList(),
    mustHaveMetatags: List<String> = emptyList(),
): BoolQueryBuilder {
    val query = BoolQueryBuilder().mustNot(existsQuery(FYLKE))

    shouldHaveMetatags.forEach { query.should(termQuery(METATAGS, it)) }
    mustHaveMetatags.forEach { query.must(termQuery(METATAGS, it)) }

    return query
}