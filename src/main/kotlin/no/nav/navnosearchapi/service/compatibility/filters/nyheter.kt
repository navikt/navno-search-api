package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.AggregationNames
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetNames
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val nyheterFilters = mapOf(
    UnderFacetKeys.STATISTIKK to FilterEntry(
        name = UnderFacetNames.STATISTIKK,
        aggregationName = AggregationNames.NYHETER_STATISTIKK,
        filterQuery = nyhetFilter(
            mustHaveMetatags = listOf(
                ValidMetatags.NYHET.descriptor,
                ValidMetatags.STATISTIKK.descriptor
            )
        ),
    ),
    UnderFacetKeys.PRESSE to FilterEntry(
        name = UnderFacetNames.PRESSE,
        filterQuery = nyhetFilter(
            shouldHaveMetatags = listOf(
                ValidMetatags.PRESSE.descriptor,
                ValidMetatags.PRESSEMELDING.descriptor
            )
        ),
    ),
    UnderFacetKeys.NAV_OG_SAMFUNN to FilterEntry(
        name = UnderFacetNames.NAV_OG_SAMFUNN,
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
    val query = BoolQueryBuilder().mustNot(ExistsQueryBuilder(FYLKE))

    shouldHaveMetatags.forEach { query.should(TermQueryBuilder(METATAGS, it)) }
    mustHaveMetatags.forEach { query.must(TermQueryBuilder(METATAGS, it)) }

    return query
}