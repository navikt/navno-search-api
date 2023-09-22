package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.search.service.search.Filter

val underfasettFilters = mapOf(
    UNDERFASETT_INFORMASJON to FilterEntry(
        name = "Informasjon",
        filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.INFORMASJON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_KONTOR to FilterEntry(
        name = "Kontor", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.KONTOR.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
        name = "Søknad og skjema", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.SKJEMA.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRIVATPERSON to FilterEntry(
        name = "Privatperson", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.PRIVATPERSON.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_ARBEIDSGIVER to FilterEntry(
        name = "Arbeidsgiver", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                audience = listOf(ValidAudiences.ARBEIDSGIVER.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = "Statistikk", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = "Presse", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSE.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_PRESSEMELDINGER to FilterEntry(
        name = "Pressemeldinger", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.PRESSEMELDING.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = "NAV og samfunn", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NAV_OG_SAMFUNN.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
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