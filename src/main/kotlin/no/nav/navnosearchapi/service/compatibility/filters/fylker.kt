package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AGDER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INNLANDET_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NORDLAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OSLO_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_OST_VIKEN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ROGALAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_TRONDELAG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VESTLAND_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_VEST_VIKEN_NAME
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val fylkeFilters = mapOf(
    UNDERFASETT_AGDER to FilterEntry(
        name = UNDERFASETT_AGDER_NAME,
        filterQuery = fylkeFilter(ValidFylker.AGDER.descriptor)
    ),
    UNDERFASETT_INNLANDET to FilterEntry(
        name = UNDERFASETT_INNLANDET_NAME,
        filterQuery = fylkeFilter(ValidFylker.INNLANDET.descriptor)
    ),
    UNDERFASETT_MORE_OG_ROMSDAL to FilterEntry(
        name = UNDERFASETT_MORE_OG_ROMSDAL_NAME,
        filterQuery = fylkeFilter(ValidFylker.MORE_OG_ROMSDAL.descriptor)
    ),
    UNDERFASETT_NORDLAND to FilterEntry(
        name = UNDERFASETT_NORDLAND_NAME,
        filterQuery = fylkeFilter(ValidFylker.NORDLAND.descriptor)
    ),
    UNDERFASETT_OSLO to FilterEntry(
        name = UNDERFASETT_OSLO_NAME,
        filterQuery = fylkeFilter(ValidFylker.OSLO.descriptor)
    ),
    UNDERFASETT_ROGALAND to FilterEntry(
        name = UNDERFASETT_ROGALAND_NAME,
        filterQuery = fylkeFilter(ValidFylker.ROGALAND.descriptor)
    ),
    UNDERFASETT_TROMS_OG_FINNMARK to FilterEntry(
        name = UNDERFASETT_TROMS_OG_FINNMARK_NAME,
        filterQuery = fylkeFilter(ValidFylker.TROMS_OG_FINNMARK.descriptor)
    ),
    UNDERFASETT_TRONDELAG to FilterEntry(
        name = UNDERFASETT_TRONDELAG_NAME,
        filterQuery = fylkeFilter(ValidFylker.TRONDELAG.descriptor)
    ),
    UNDERFASETT_VESTFOLD_OG_TELEMARK to FilterEntry(
        name = UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME,
        filterQuery = fylkeFilter(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor)
    ),
    UNDERFASETT_VESTLAND to FilterEntry(
        name = UNDERFASETT_VESTLAND_NAME,
        filterQuery = fylkeFilter(ValidFylker.VESTLAND.descriptor)
    ),
    UNDERFASETT_VEST_VIKEN to FilterEntry(
        name = UNDERFASETT_VEST_VIKEN_NAME,
        filterQuery = fylkeFilter(ValidFylker.VEST_VIKEN.descriptor)
    ),
    UNDERFASETT_OST_VIKEN to FilterEntry(
        name = UNDERFASETT_OST_VIKEN_NAME,
        filterQuery = fylkeFilter(ValidFylker.OST_VIKEN.descriptor)
    ),
)

private fun fylkeFilter(requiredFylke: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .must(termQuery(FYLKE, requiredFylke))
        .mustNot(isFileFilter())
}