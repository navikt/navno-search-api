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
import no.nav.navnosearchapi.search.service.search.Filter

val fylkeFilters = mapOf(
    UNDERFASETT_AGDER to FilterEntry( // todo: generaliser slike filtre som har mye til felles
        name = UNDERFASETT_AGDER_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.AGDER.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_INNLANDET to FilterEntry(
        name = UNDERFASETT_INNLANDET_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.INNLANDET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_MORE_OG_ROMSDAL to FilterEntry(
        name = UNDERFASETT_MORE_OG_ROMSDAL_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.MORE_OG_ROMSDAL.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_NORDLAND to FilterEntry(
        name = UNDERFASETT_NORDLAND_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.NORDLAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_OSLO to FilterEntry(
        name = UNDERFASETT_OSLO_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.OSLO.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_ROGALAND to FilterEntry(
        name = UNDERFASETT_ROGALAND_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.ROGALAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_TROMS_OG_FINNMARK to FilterEntry(
        name = UNDERFASETT_TROMS_OG_FINNMARK_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.TROMS_OG_FINNMARK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_TRONDELAG to FilterEntry(
        name = UNDERFASETT_TRONDELAG_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.TRONDELAG.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VESTFOLD_OG_TELEMARK to FilterEntry(
        name = UNDERFASETT_VESTFOLD_OG_TELEMARK_NAME,
        filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VESTLAND to FilterEntry(
        name = UNDERFASETT_VESTLAND_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VESTLAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VEST_VIKEN to FilterEntry(
        name = UNDERFASETT_VEST_VIKEN_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VEST_VIKEN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_OST_VIKEN to FilterEntry(
        name = UNDERFASETT_OST_VIKEN_NAME, filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.OST_VIKEN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)