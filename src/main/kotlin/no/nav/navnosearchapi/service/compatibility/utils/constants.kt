package no.nav.navnosearchapi.service.compatibility.utils

object FacetKeys {
    const val PRIVATPERSON = "0"
    const val ARBEIDSGIVER = "1"
    const val SAMARBEIDSPARTNER = "2"
    const val NYHETER = "3"
    const val STATISTIKK = "4"
    const val ANALYSER_OG_FORSKNING = "5"
    const val INNHOLD_FRA_FYLKER = "6"
}

object FacetNames {
    const val PRIVATPERSON = "Privatperson"
    const val ARBEIDSGIVER = "Arbeidsgiver"
    const val SAMARBEIDSPARTNER = "Samarbeidspartner"
    const val NYHETER = "Nyheter"
    const val STATISTIKK = "Statistikk"
    const val ANALYSER_OG_FORSKNING = "Analyser og forskning"
    const val INNHOLD_FRA_FYLKER = "Innhold fra fylker"
}

object UnderFacetKeys {
    const val INFORMASJON = "0"
    const val KONTOR = "1"
    const val SOKNAD_OG_SKJEMA = "2"
    const val AKTUELT = "3"
    const val PRESSE = "0"
    const val NAV_OG_SAMFUNN = "1"
    const val STATISTIKK = "2"
    const val AGDER = "0"
    const val INNLANDET = "1"
    const val MORE_OG_ROMSDAL = "2"
    const val NORDLAND = "3"
    const val OSLO = "4"
    const val ROGALAND = "5"
    const val TROMS_OG_FINNMARK = "6"
    const val TRONDELAG = "7"
    const val VESTFOLD_OG_TELEMARK = "8"
    const val VESTLAND = "9"
    const val VEST_VIKEN = "10"
    const val OST_VIKEN = "11"
}

object UnderFacetNames {
    const val INFORMASJON = "Informasjon"
    const val KONTOR = "Kontor"
    const val SOKNAD_OG_SKJEMA = "Søknad og skjema"
    const val AKTUELT = "Aktuelt"
    const val PRESSE = "Presse"
    const val NAV_OG_SAMFUNN = "NAV og samfunn"
    const val STATISTIKK = "Statistikk"
    const val AGDER = "Agder"
    const val INNLANDET = "Innlandet"
    const val MORE_OG_ROMSDAL = "Møre og Romsdal"
    const val NORDLAND = "Nordland"
    const val OSLO = "Oslo"
    const val ROGALAND = "Rogaland"
    const val TROMS_OG_FINNMARK = "Troms og Finnmark"
    const val TRONDELAG = "Trøndelag"
    const val VESTFOLD_OG_TELEMARK = "Vestfold og Telemark"
    const val VESTLAND = "Vestland"
    const val VEST_VIKEN = "Vest-Viken"
    const val OST_VIKEN = "Øst-Viken"
}

object AggregationNames {
    const val NYHETER_STATISTIKK = FacetNames.NYHETER + UnderFacetNames.STATISTIKK
    const val PRIVATPERSON_INFORMASJON = FacetNames.PRIVATPERSON + UnderFacetNames.INFORMASJON
    const val PRIVATPERSON_KONTOR = FacetNames.PRIVATPERSON + UnderFacetNames.KONTOR
    const val PRIVATPERSON_SOKNAD_OG_SKJEMA = FacetNames.PRIVATPERSON + UnderFacetNames.SOKNAD_OG_SKJEMA
    const val PRIVATPERSON_AKTUELT = FacetNames.PRIVATPERSON + UnderFacetNames.AKTUELT
    const val ARBEIDSGIVER_INFORMASJON = FacetNames.ARBEIDSGIVER + UnderFacetNames.INFORMASJON
    const val ARBEIDSGIVER_KONTOR = FacetNames.ARBEIDSGIVER + UnderFacetNames.KONTOR
    const val ARBEIDSGIVER_SOKNAD_OG_SKJEMA = FacetNames.ARBEIDSGIVER + UnderFacetNames.SOKNAD_OG_SKJEMA
    const val ARBEIDSGIVER_AKTUELT = FacetNames.ARBEIDSGIVER + UnderFacetNames.AKTUELT
    const val SAMARBEIDSPARTNER_INFORMASJON = FacetNames.SAMARBEIDSPARTNER + UnderFacetNames.INFORMASJON
    const val SAMARBEIDSPARTNER_KONTOR = FacetNames.SAMARBEIDSPARTNER + UnderFacetNames.KONTOR
    const val SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA = FacetNames.SAMARBEIDSPARTNER + UnderFacetNames.SOKNAD_OG_SKJEMA
    const val SAMARBEIDSPARTNER_AKTUELT = FacetNames.SAMARBEIDSPARTNER + UnderFacetNames.AKTUELT
}