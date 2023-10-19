package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_AGDER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INNLANDET_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NORDLAND_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OSLO_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OST_VIKEN_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ROGALAND_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TRONDELAG_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTLAND_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VEST_VIKEN_NAME
import no.nav.navnosearchapi.search.search.Filter

val fylkeFilters = mapOf(
    UNDERFASETT_AGDER to fylkeFilter(
        name = UNDERFASETT_AGDER_NAME,
        requiredFylke = ValidFylker.AGDER.descriptor
    ),
    UNDERFASETT_INNLANDET to fylkeFilter(
        name = UNDERFASETT_INNLANDET_NAME,
        requiredFylke = ValidFylker.INNLANDET.descriptor
    ),
    UNDERFASETT_MORE_OG_ROMSDAL to fylkeFilter(
        name = UNDERFASETT_MORE_OG_ROMSDAL_NAME,
        requiredFylke = ValidFylker.MORE_OG_ROMSDAL.descriptor
    ),
    UNDERFASETT_NORDLAND to fylkeFilter(
        name = UNDERFASETT_NORDLAND_NAME,
        requiredFylke = ValidFylker.NORDLAND.descriptor
    ),
    UNDERFASETT_OSLO to fylkeFilter(
        name = UNDERFASETT_OSLO_NAME,
        requiredFylke = ValidFylker.OSLO.descriptor
    ),
    UNDERFASETT_ROGALAND to fylkeFilter(
        name = UNDERFASETT_ROGALAND_NAME,
        requiredFylke = ValidFylker.ROGALAND.descriptor
    ),
    UNDERFASETT_TROMS_OG_FINNMARK to fylkeFilter(
        name = UNDERFASETT_TROMS_OG_FINNMARK_NAME,
        requiredFylke = ValidFylker.TROMS_OG_FINNMARK.descriptor
    ),
    UNDERFASETT_TRONDELAG to fylkeFilter(
        name = UNDERFASETT_TRONDELAG_NAME,
        requiredFylke = ValidFylker.TRONDELAG.descriptor
    ),
    UNDERFASETT_VESTFOLD_OG_TELEMARK to fylkeFilter(
        name = UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME,
        requiredFylke = ValidFylker.VESTFOLD_OG_TELEMARK.descriptor
    ),
    UNDERFASETT_VESTLAND to fylkeFilter(
        name = UNDERFASETT_VESTLAND_NAME,
        requiredFylke = ValidFylker.VESTLAND.descriptor
    ),
    UNDERFASETT_VEST_VIKEN to fylkeFilter(
        name = UNDERFASETT_VEST_VIKEN_NAME,
        requiredFylke = ValidFylker.VEST_VIKEN.descriptor
    ),
    UNDERFASETT_OST_VIKEN to fylkeFilter(
        name = UNDERFASETT_OST_VIKEN_NAME,
        requiredFylke = ValidFylker.OST_VIKEN.descriptor
    ),
)

private fun fylkeFilter(name: String, requiredFylke: String): FilterEntry {
    return FilterEntry(
        name = name,
        filters = listOf(Filter(fylke = listOf(requiredFylke), isFile = listOf(false.toString())).toQuery())
    )
}