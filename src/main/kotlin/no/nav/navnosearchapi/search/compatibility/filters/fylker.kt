package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.search.service.search.Filter

val fylkeFilters = mapOf(
    UNDERFASETT_AGDER to FilterEntry( // todo: generaliser slike filtre som har mye til felles
        name = "Agder", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.AGDER.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_INNLANDET to FilterEntry(
        name = "Innlandet", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.INNLANDET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_MORE_OG_ROMSDAL to FilterEntry(
        name = "Møre og Romsdal", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.MORE_OG_ROMSDAL.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_NORDLAND to FilterEntry(
        name = "Nordland", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.NORDLAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_OSLO to FilterEntry(
        name = "Oslo", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.OSLO.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_ROGALAND to FilterEntry(
        name = "Rogaland", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.ROGALAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_TROMS_OG_FINNMARK to FilterEntry(
        name = "Troms og Finnmark", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.TROMS_OG_FINNMARK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_TRONDELAG to FilterEntry(
        name = "Trøndelag", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.TRONDELAG.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VESTFOLD_OG_TELEMARK to FilterEntry(
        name = "Vestfold og Telemark",
        filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VESTLAND to FilterEntry(
        name = "Vestland", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VESTLAND.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_VEST_VIKEN to FilterEntry(
        name = "Vest-Viken", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.VEST_VIKEN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_OST_VIKEN to FilterEntry(
        name = "Øst-Viken", filters = listOf(
            Filter(
                fylke = listOf(ValidFylker.OST_VIKEN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
)