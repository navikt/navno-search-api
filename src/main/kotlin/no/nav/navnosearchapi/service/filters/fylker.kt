package no.nav.navnosearchapi.service.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchapi.service.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.utils.UnderFacetNames
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val fylkeFilters = mapOf(
    UnderFacetKeys.AGDER to FilterEntry(
        name = UnderFacetNames.AGDER,
        filterQuery = fylkeFilter(ValidFylker.AGDER.descriptor)
    ),
    UnderFacetKeys.INNLANDET to FilterEntry(
        name = UnderFacetNames.INNLANDET,
        filterQuery = fylkeFilter(ValidFylker.INNLANDET.descriptor)
    ),
    UnderFacetKeys.MORE_OG_ROMSDAL to FilterEntry(
        name = UnderFacetNames.MORE_OG_ROMSDAL,
        filterQuery = fylkeFilter(ValidFylker.MORE_OG_ROMSDAL.descriptor)
    ),
    UnderFacetKeys.NORDLAND to FilterEntry(
        name = UnderFacetNames.NORDLAND,
        filterQuery = fylkeFilter(ValidFylker.NORDLAND.descriptor)
    ),
    UnderFacetKeys.OSLO to FilterEntry(
        name = UnderFacetNames.OSLO,
        filterQuery = fylkeFilter(ValidFylker.OSLO.descriptor)
    ),
    UnderFacetKeys.ROGALAND to FilterEntry(
        name = UnderFacetNames.ROGALAND,
        filterQuery = fylkeFilter(ValidFylker.ROGALAND.descriptor)
    ),
    UnderFacetKeys.TROMS_OG_FINNMARK to FilterEntry(
        name = UnderFacetNames.TROMS_OG_FINNMARK,
        filterQuery = fylkeFilter(ValidFylker.TROMS_OG_FINNMARK.descriptor)
    ),
    UnderFacetKeys.TRONDELAG to FilterEntry(
        name = UnderFacetNames.TRONDELAG,
        filterQuery = fylkeFilter(ValidFylker.TRONDELAG.descriptor)
    ),
    UnderFacetKeys.VESTFOLD_OG_TELEMARK to FilterEntry(
        name = UnderFacetNames.VESTFOLD_OG_TELEMARK,
        filterQuery = fylkeFilter(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor)
    ),
    UnderFacetKeys.VESTLAND to FilterEntry(
        name = UnderFacetNames.VESTLAND,
        filterQuery = fylkeFilter(ValidFylker.VESTLAND.descriptor)
    ),
    UnderFacetKeys.VEST_VIKEN to FilterEntry(
        name = UnderFacetNames.VEST_VIKEN,
        filterQuery = fylkeFilter(ValidFylker.VEST_VIKEN.descriptor)
    ),
    UnderFacetKeys.OST_VIKEN to FilterEntry(
        name = UnderFacetNames.OST_VIKEN,
        filterQuery = fylkeFilter(ValidFylker.OST_VIKEN.descriptor)
    ),
)

private fun fylkeFilter(requiredFylke: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .must(TermQueryBuilder(FYLKE, requiredFylke))
}