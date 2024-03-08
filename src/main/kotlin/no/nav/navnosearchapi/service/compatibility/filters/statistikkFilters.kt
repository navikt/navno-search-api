package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.AggregationNames
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetNames
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val statistikkFilters = mapOf(
    UnderFacetKeys.ARTIKLER to FilterEntry(
        name = UnderFacetNames.ARTIKLER,
        aggregationName = AggregationNames.STATISTIKK_ARTIKLER,
        filterQuery = baseQuery().mustNot(TermQueryBuilder(TYPE, ValidTypes.TABELL.descriptor))
            .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
    ),
    UnderFacetKeys.NYHETER to FilterEntry(
        name = UnderFacetNames.NYHETER,
        aggregationName = AggregationNames.STATISTIKK_NYHETER,
        filterQuery = baseQuery().must(
            BoolQueryBuilder()
                .should(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
                .should(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        )
    ),
    UnderFacetKeys.TABELLER to FilterEntry(
        name = UnderFacetNames.TABELLER,
        aggregationName = AggregationNames.STATISTIKK_TABELLER,
        filterQuery = baseQuery().must(TermQueryBuilder(TYPE, ValidTypes.TABELL.descriptor))
    ),
)

private fun baseQuery() = BoolQueryBuilder().must(TermQueryBuilder(METATAGS, ValidMetatags.STATISTIKK.descriptor))