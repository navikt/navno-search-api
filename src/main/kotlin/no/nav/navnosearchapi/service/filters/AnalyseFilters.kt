package no.nav.navnosearchapi.service.filters

import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.utils.AggregationNames
import no.nav.navnosearchapi.service.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.utils.UnderFacetNames
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val analyseFilters = mapOf(
    UnderFacetKeys.ARTIKLER to FilterEntry(
        name = UnderFacetNames.ARTIKLER,
        aggregationName = AggregationNames.ANALYSER_OG_FORSKNING_ARTIKLER,
        filterQuery = baseQuery().mustNot(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
    ),
    UnderFacetKeys.NYHETER to FilterEntry(
        name = UnderFacetNames.NYHETER,
        aggregationName = AggregationNames.ANALYSER_OG_FORSKNING_NYHETER,
        filterQuery = baseQuery().must(
            BoolQueryBuilder()
                .should(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
                .should(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        )
    )
)

private fun baseQuery() = BoolQueryBuilder().must(TermQueryBuilder(METATAGS, ValidMetatags.ANALYSE.descriptor))