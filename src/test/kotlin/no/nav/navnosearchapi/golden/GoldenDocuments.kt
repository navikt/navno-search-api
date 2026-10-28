package no.nav.navnosearchapi.golden

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.Content
import java.time.ZonedDateTime

// Testdataene til golden-testene (se GoldenFixtures). Faste id-er og tidspunkter, så indeksen blir lik hver gang.

private val SOFT_HYPHEN = Char(0xAD)
private val GRINNING_FACE = String(Character.toChars(0x1F600))

// Lengre enn de 250 tegnene API-et kutter lange ingresser ved, med søkeordet helt til slutt
private const val MELDEKORT_INGRESS =
    "Når du får dagpenger, tiltakspenger eller arbeidsavklaringspenger, må du vanligvis sende inn et elektronisk " +
        "skjema hver fjortende dag. Vi bruker skjemaet til å beregne hvor mye du skal få utbetalt, og det må sendes " +
        "inn innen fristen for at du skal få pengene i tide. Dette skjemaet heter meldekort."

// Akkurat så lang at emojien (to UTF-16-tegn) havner midt i kuttet ved 250 tegn
private val EMOJI_INGRESS =
    "Denne ingressen er akkurat lang nok til at emojien havner midt i kuttet. ".repeat(4).take(249) +
        GRINNING_FACE + " Slutt."

private fun doc(
    id: String,
    path: String,
    title: String,
    ingress: String,
    text: String = ingress,
    type: ValidTypes = ValidTypes.ANDRE,
    audience: List<ValidAudiences> = listOf(ValidAudiences.PERSON),
    metatags: List<ValidMetatags> = listOf(ValidMetatags.INFORMASJON),
    fylke: String? = null,
    language: String = NORWEGIAN_BOKMAAL,
    languageRefs: List<String> = emptyList(),
    keywords: List<String> = emptyList(),
    createdAt: String = "2024-01-15T10:30:00.120+01:00",
    lastUpdated: String = createdAt,
) = Content.from(
    id = "golden-$id",
    teamOwnedBy = "golden",
    href = "https://www.nav.no/$path",
    title = title,
    ingress = ingress,
    text = text,
    type = type.descriptor,
    createdAt = ZonedDateTime.parse(createdAt),
    lastUpdated = ZonedDateTime.parse(lastUpdated),
    sortByDate = ZonedDateTime.parse(lastUpdated),
    audience = audience.map { it.descriptor },
    language = language,
    fylke = fylke,
    metatags = metatags.map { it.descriptor },
    keywords = keywords,
    languageRefs = languageRefs,
)

private val privatperson = listOf(
    doc(
        "dagpenger", "dagpenger", "Dagpenger",
        "Dagpenger skal sikre deg inntekt når du er arbeidsledig eller permittert.",
        "Du kan få dagpenger hvis du har mistet jobben eller er permittert. Du må være registrert som arbeidssøker " +
            "og sende meldekort hver fjortende dag.",
        type = ValidTypes.PRODUKTSIDE,
        languageRefs = listOf(NORWEGIAN_NYNORSK, ENGLISH),
        keywords = listOf("arbeidsledig", "permittert"),
        createdAt = "2023-03-28T15:28:58.022+02:00",
        lastUpdated = "2024-01-02T10:46:08.120+01:00",
    ),
    doc(
        "aap", "aap", "Arbeidsavklaringspenger (AAP)",
        "AAP skal sikre deg inntekt når du har nedsatt arbeidsevne på grunn av sykdom eller skade.",
        "Du kan få AAP hvis arbeidsevnen din er nedsatt med minst halvparten, og du får behandling eller deltar " +
            "på et arbeidsrettet tiltak.",
        type = ValidTypes.PRODUKTSIDE,
        createdAt = "2023-05-10T09:00:00.000+02:00",
    ),
    doc(
        "sykepenger", "sykepenger", "Sykepenger",
        "Sykepenger erstatter inntekten din når du ikke kan jobbe på grunn av sykdom eller skade.",
        "Du må vanligvis ha vært i arbeid i minst fire uker for å ha rett til sykepenger. Arbeidsgiveren betaler " +
            "de første 16 dagene.",
        type = ValidTypes.PRODUKTSIDE,
        createdAt = "2024-06-01T08:00:00.500+02:00",
    ),
    doc(
        "foreldrepenger", "foreldrepenger", "Foreldrepenger",
        "Foreldrepenger skal erstatte inntekten din når du er hjemme med barn.",
        "Du kan få foreldrepenger når du får eller adopterer barn. Begge foreldrene må ha hatt inntekt i minst " +
            "seks av de siste ti månedene.",
        type = ValidTypes.PRODUKTSIDE,
        createdAt = "2022-11-11T11:11:11.111+01:00",
    ),
    doc(
        "barnetrygd", "barnetrygd", "Barnetrygd",
        "Barnetrygd er et bidrag til forsørgelse av barn under 18 år.",
        "Du får barnetrygd automatisk når barnet blir født, hvis du er bosatt i Norge.",
        type = ValidTypes.PRODUKTSIDE,
        audience = listOf(ValidAudiences.PERSON, ValidAudiences.OTHER),
        createdAt = "2024-03-01T00:00:00Z",
    ),
    doc(
        "alderspensjon", "pensjon", "Alderspensjon",
        "Alderspensjon kan du ta ut fra du er 62 år.",
        "Du kan kombinere alderspensjon og arbeid uten at pensjonen blir redusert.",
        type = ValidTypes.TEMASIDE,
        createdAt = "2021-09-30T23:59:59.999Z",
    ),
    doc(
        "kontakt-oss", "kontaktoss", "Kontakt oss",
        "Her finner du telefonnumre og chat, og du kan skrive til oss.",
        "Du kan ringe oss på telefon 55 55 33 33, chatte med oss eller skrive til oss på Min side.",
        type = ValidTypes.SITUASJONSSIDE,
        keywords = listOf("telefon", "chat"),
        createdAt = "2023-01-09T08:30:00.000+01:00",
        lastUpdated = "2024-08-20T12:00:00.000+02:00",
    ),
    doc(
        "meldekort", "meldekort", "Meldekort",
        MELDEKORT_INGRESS,
        "Du sender meldekort på Min side, i appen eller på papir.",
        type = ValidTypes.GUIDE,
        createdAt = "2022-05-17T17:05:00.000+02:00",
    ),
    doc(
        "utbetalinger", "utbetalinger", "Utbetalinger",
        "Se når pengene kommer og hvor mye du får.",
        "Utbetalingene kommer vanligvis den 20. i måneden. Skattetrekk blir trukket fra før pengene settes inn på " +
            "kontoen din.",
        createdAt = "2023-08-01T07:00:00.000+02:00",
    ),
    doc(
        "tilleggsstonader", "tilleggsstonader", "Tilleggsstønader",
        "Støtte til utgifter mens du deltar på et tiltak.",
        "Les om hva du kan få dekket.",
        createdAt = "2023-08-02T07:00:00.000+02:00",
    ),
    doc(
        "grunnstonad", "grunnstonad", "Grunnstønad",
        "Grunn${SOFT_HYPHEN}stønad kan du få hvis du har ekstrautgifter på grunn av varig sykdom.",
        "Grunnstønaden skal dekke ekstrautgifter, for eksempel til transport og klær.",
        createdAt = "2023-08-03T07:00:00.000+02:00",
    ),
    doc(
        "hjelpemidler", "hjelpemidler", "Hjelpemidler",
        "Hjelpemidler & tilrettelegging for deg som har <nedsatt> funksjonsevne.",
        "Du kan låne hjelpemidler fra Nav hjelpemiddelsentral.",
        createdAt = "2023-08-04T07:00:00.000+02:00",
    ),
    doc(
        "uten-malgruppe", "informasjon-uten-malgruppe", "Informasjon uten målgruppe",
        "Denne siden har ingen målgruppe, og vises derfor for alle.",
        audience = emptyList(),
        createdAt = "2023-08-05T07:00:00.000+02:00",
    ),
    doc(
        "annen-malgruppe", "informasjon-for-andre", "Informasjon for andre",
        "Denne siden er merket med målgruppen andre.",
        audience = listOf(ValidAudiences.OTHER),
        createdAt = "2023-08-06T07:00:00.000+02:00",
    ),
    doc(
        "permittering", "permittering", "Permittering",
        "Informasjon om permittering for deg som er permittert og for arbeidsgivere.",
        "Når du er permittert, kan du ha rett til dagpenger. Arbeidsgiveren må betale lønn de første dagene.",
        audience = listOf(ValidAudiences.PERSON, ValidAudiences.EMPLOYER),
        createdAt = "2020-03-15T20:00:00.000+01:00",
        lastUpdated = "2024-02-29T09:00:00.000+01:00",
    ),
    doc(
        "nav-grunerlokka", "kontor/nav-grunerlokka", "Nav Grünerløkka",
        "Nav Grünerløkka holder til i Thorvald Meyers gate.",
        type = ValidTypes.KONTOR,
        metatags = emptyList(),
        createdAt = "2023-09-01T10:00:00.000+02:00",
    ),
    doc(
        "nav-frogner", "kontor/nav-frogner", "Nav Frogner",
        "Nav Frogner er Nav-kontoret for bydel Frogner.",
        type = ValidTypes.KONTOR_LEGACY,
        metatags = emptyList(),
        createdAt = "2023-09-02T10:00:00.000+02:00",
    ),
    doc(
        "soknad-dagpenger", "soknader/dagpenger", "Søknad om dagpenger (NAV 04-01.03)",
        "Søk om dagpenger når du er arbeidsledig eller permittert.",
        type = ValidTypes.SKJEMA,
        metatags = emptyList(),
        createdAt = "2023-09-03T10:00:00.000+02:00",
    ),
    doc(
        "soknad-tilleggsstonader", "soknader/tilleggsstonader", "Søknad om tilleggsstønader (NAV 09-35.01)",
        "Søk om tilleggsstønader til reise og pass av barn mens du deltar på tiltak.",
        type = ValidTypes.SKJEMA,
        metatags = emptyList(),
        createdAt = "2023-09-04T10:00:00.000+02:00",
    ),
    doc(
        "skjemaoversikt", "soknader", "Søknader og skjemaer",
        "Her finner du alle søknader og skjemaer for privatpersoner.",
        type = ValidTypes.SKJEMAOVERSIKT,
        metatags = emptyList(),
        createdAt = "2023-09-05T10:00:00.000+02:00",
    ),
    doc(
        "nyhet-satser", "nyheter/nye-satser", "Nye satser for dagpenger",
        "Fra 1. januar øker satsene for dagpenger.",
        type = ValidTypes.AKTUELT,
        metatags = listOf(ValidMetatags.NYHET),
        createdAt = "2024-12-02T09:00:00.000+01:00",
        lastUpdated = "2024-12-05T14:30:00.500+01:00",
    ),
    doc(
        "nyhet-sykepenger", "nyheter/sykepenger", "Endringer i sykepenger fra nyttår",
        "Reglene for sykepenger endres fra nyttår.",
        type = ValidTypes.AKTUELT,
        metatags = listOf(ValidMetatags.NYHET),
        createdAt = "2024-11-20T12:00:00.000+01:00",
    ),
)

private val arbeidsgiver = listOf(
    doc(
        "sykefravar-arbeidsgiver", "arbeidsgiver/sykefravar", "Sykefravær for arbeidsgivere",
        "Dette må du som arbeidsgiver gjøre når en ansatt er syk.",
        audience = listOf(ValidAudiences.EMPLOYER),
        createdAt = "2023-10-01T10:00:00.000+02:00",
    ),
    doc(
        "arbeidslivssenter", "arbeidsgiver/arbeidslivssenter", "Nav arbeidslivssenter Oslo",
        "Arbeidslivssenteret hjelper virksomheter med å forebygge sykefravær.",
        type = ValidTypes.KONTOR,
        audience = listOf(ValidAudiences.EMPLOYER),
        metatags = emptyList(),
        createdAt = "2023-10-02T10:00:00.000+02:00",
    ),
    doc(
        "refusjon-sykepenger", "arbeidsgiver/refusjon", "Refusjon av sykepenger (NAV 08-20.12)",
        "Krev refusjon av sykepenger du har betalt til en ansatt.",
        type = ValidTypes.SKJEMA,
        audience = listOf(ValidAudiences.EMPLOYER),
        metatags = emptyList(),
        createdAt = "2023-10-03T10:00:00.000+02:00",
    ),
    doc(
        "nyhet-permittering", "arbeidsgiver/nyheter/permittering", "Nye regler for permittering",
        "Reglene for permittering endres fra 1. mars.",
        type = ValidTypes.AKTUELT,
        audience = listOf(ValidAudiences.EMPLOYER),
        metatags = listOf(ValidMetatags.NYHET),
        createdAt = "2025-02-01T10:00:00.000+01:00",
        lastUpdated = "2025-02-03T10:00:00.250+01:00",
    ),
)

private val samarbeidspartner = listOf(
    doc(
        "leger", "samarbeidspartner/leger", "Informasjon til leger",
        "Det du som lege trenger å vite om sykmelding og legeerklæringer.",
        audience = listOf(ValidAudiences.PROVIDER, ValidAudiences.PROVIDER_DOCTOR),
        createdAt = "2023-11-01T10:00:00.000+01:00",
    ),
    doc(
        "kommune", "samarbeidspartner/kommune", "Informasjon til ansatte i kommunen",
        "Om samarbeidet mellom Nav og kommunen om oppfølging av brukere.",
        audience = listOf(
            ValidAudiences.PROVIDER,
            ValidAudiences.PROVIDER_MUNICIPALITY_EMPLOYED,
            ValidAudiences.PROVIDER_OTHER,
        ),
        createdAt = "2023-11-02T10:00:00.000+01:00",
    ),
    doc(
        "legeerklaring", "samarbeidspartner/legeerklaring", "Legeerklæring ved arbeidsuførhet (NAV 08-07.08)",
        "Skjemaet brukes når legen skal dokumentere arbeidsuførhet.",
        type = ValidTypes.SKJEMA,
        audience = listOf(ValidAudiences.PROVIDER, ValidAudiences.PROVIDER_DOCTOR),
        metatags = emptyList(),
        createdAt = "2023-11-03T10:00:00.000+01:00",
    ),
    doc(
        "nyhet-samarbeid", "samarbeidspartner/nyheter", "Nytt fra Nav til samarbeidspartnere",
        "Nyheter for leger, kommuner og tiltaksarrangører.",
        type = ValidTypes.AKTUELT,
        audience = listOf(ValidAudiences.PROVIDER),
        metatags = listOf(ValidMetatags.NYHET),
        createdAt = "2024-08-15T13:00:00.000+02:00",
    ),
    doc(
        "emojitest", "samarbeidspartner/emojitest", "Emojitest",
        EMOJI_INGRESS,
        "Tekst.",
        type = ValidTypes.KONTOR,
        audience = listOf(ValidAudiences.PROVIDER),
        metatags = emptyList(),
        createdAt = "2023-11-04T10:00:00.000+01:00",
    ),
)

private val presseStatistikkOgAnalyse = listOf(
    doc(
        "pressemelding-ledighet", "presse/ledighet", "Ledigheten går ned i oktober",
        "Arbeidsledigheten falt til 2,1 prosent i oktober.",
        type = ValidTypes.AKTUELT,
        audience = emptyList(),
        metatags = listOf(ValidMetatags.PRESSEMELDING),
        createdAt = "2024-10-31T08:00:00.000+01:00",
        lastUpdated = "2024-10-31T09:15:00.000+01:00",
    ),
    doc(
        "presserom", "presse", "Presserom",
        "Kontaktinformasjon for journalister og pressemeldinger fra Nav.",
        audience = emptyList(),
        metatags = listOf(ValidMetatags.PRESSE),
        createdAt = "2023-12-01T10:00:00.000+01:00",
    ),
    doc(
        "hovedtall", "statistikk/hovedtall", "Hovedtall om arbeidsmarkedet",
        "Månedlig statistikk om arbeidsledige og ledige stillinger.",
        audience = emptyList(),
        metatags = listOf(ValidMetatags.STATISTIKK),
        createdAt = "2023-12-02T10:00:00.000+01:00",
    ),
    doc(
        "statistikknyhet", "statistikk/nyheter/flere-i-arbeid", "Flere i arbeid i september",
        "Statistikken viser at flere er i arbeid enn i fjor.",
        type = ValidTypes.AKTUELT,
        audience = emptyList(),
        metatags = listOf(ValidMetatags.STATISTIKK, ValidMetatags.NYHET),
        createdAt = "2024-10-04T08:00:00.000+02:00",
        lastUpdated = "2024-10-07T15:45:30.010+02:00",
    ),
    doc(
        "tabell-ledige", "statistikk/tabeller/helt-ledige", "Helt ledige etter fylke",
        "Tabell over helt ledige fordelt på fylke.",
        type = ValidTypes.TABELL,
        audience = emptyList(),
        metatags = listOf(ValidMetatags.STATISTIKK),
        createdAt = "2023-12-03T10:00:00.000+01:00",
    ),
    doc(
        "analyse-sykefravar", "analyse/sykefravar", "Analyse av sykefraværet",
        "En analyse av utviklingen i sykefraværet de siste ti årene.",
        audience = emptyList(),
        metatags = listOf(ValidMetatags.ANALYSE),
        createdAt = "2023-12-04T10:00:00.000+01:00",
    ),
    doc(
        "analyse-nyhet", "analyse/nyheter/uforetrygd", "Ny analyse om uføretrygd",
        "Ny analyse viser at færre unge får uføretrygd.",
        type = ValidTypes.AKTUELT,
        audience = emptyList(),
        metatags = listOf(ValidMetatags.ANALYSE, ValidMetatags.NYHET),
        createdAt = "2023-12-05T10:00:00.000+01:00",
    ),
)

private val sprak = listOf(
    doc(
        "dagpenger-en", "en/home/benefits/unemployment-benefits", "Unemployment benefits",
        "Unemployment benefits secure your income when you are unemployed or laid off.",
        type = ValidTypes.PRODUKTSIDE,
        language = ENGLISH,
        languageRefs = listOf(NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK),
        createdAt = "2023-03-28T15:30:00.000+02:00",
    ),
    doc(
        "dagpenger-nn", "dagpenger/nn", "Dagpengar",
        "Dagpengar skal sikre deg inntekt når du er arbeidsledig eller permittert.",
        type = ValidTypes.PRODUKTSIDE,
        language = NORWEGIAN_NYNORSK,
        languageRefs = listOf(NORWEGIAN_BOKMAAL, ENGLISH),
        createdAt = "2023-03-28T15:31:00.000+02:00",
    ),
    doc(
        "dagpenger-se", "dagpenger/se", "Bargguhisvuođadoarja",
        "Bargguhisvuođadoarja sihkkarastá du dietnasa go leat bargguhis.",
        language = "se",
        languageRefs = listOf(NORWEGIAN_BOKMAAL),
        createdAt = "2023-03-28T15:32:00.000+02:00",
    ),
    doc(
        "english-only", "en/home", "Information in English",
        "Information about Nav for people who do not read Norwegian.",
        language = ENGLISH,
        createdAt = "2023-03-28T15:33:00.000+02:00",
    ),
)

private fun ValidFylker.displayName() = when (this) {
    ValidFylker.AGDER -> "Agder"
    ValidFylker.INNLANDET -> "Innlandet"
    ValidFylker.MORE_OG_ROMSDAL -> "Møre og Romsdal"
    ValidFylker.NORDLAND -> "Nordland"
    ValidFylker.OSLO -> "Oslo"
    ValidFylker.ROGALAND -> "Rogaland"
    ValidFylker.TROMS_OG_FINNMARK -> "Troms og Finnmark"
    ValidFylker.TRONDELAG -> "Trøndelag"
    ValidFylker.VESTFOLD_OG_TELEMARK -> "Vestfold og Telemark"
    ValidFylker.VESTLAND -> "Vestland"
    ValidFylker.VEST_VIKEN -> "Vest-Viken"
    ValidFylker.OST_VIKEN -> "Øst-Viken"
}

private val fylker = ValidFylker.entries.mapIndexed { i, fylke ->
    doc(
        "fylke-${fylke.descriptor}", "${fylke.descriptor}/arbeidsmarkedet", "Arbeidsmarkedet i ${fylke.displayName()}",
        "Slik er arbeidsmarkedet i ${fylke.displayName()} akkurat nå.",
        fylke = fylke.descriptor,
        metatags = emptyList(),
        createdAt = "2024-04-%02dT10:00:00.000+02:00".format(i + 1),
    )
} + doc(
    // Et blankt fylke telles som et fylke av filtrene, men ikke når tidspunktene skal vises
    "blankt-fylke", "arbeidsmarkedet", "Arbeidsmarkedet i hele landet",
    "Slik er arbeidsmarkedet i hele landet akkurat nå.",
    fylke = "  ",
    metatags = emptyList(),
    createdAt = "2024-04-20T10:00:00.000+02:00",
)

// Flere sider, så privatperson får mer enn to sider med treff
private val flereSider = listOf(
    Triple("kontantstotte", "Kontantstøtte", "Kontantstøtte kan du få for barn mellom ett og to år som ikke går i barnehage med offentlig tilskudd."),
    Triple("engangsstonad", "Engangsstønad", "Engangsstønad er en skattefri sum du kan få for hvert barn hvis du ikke har rett til foreldrepenger."),
    Triple("uforetrygd", "Uføretrygd", "Uføretrygd skal sikre inntekt for deg som har varig nedsatt arbeidsevne på grunn av sykdom eller skade."),
    Triple("gjenlevendepensjon", "Pensjon til gjenlevende ektefelle", "Du kan ha rett til pensjon hvis ektefellen, partneren eller samboeren din dør."),
    Triple("hjelpestonad", "Hjelpestønad", "Hjelpestønad kan du få hvis du har et særskilt behov for tilsyn og pleie."),
    Triple("barnebidrag", "Barnebidrag", "Barnebidrag skal sikre at begge foreldrene bidrar til å forsørge barnet."),
    Triple("pleiepenger", "Pleiepenger for sykt barn", "Pleiepenger skal erstatte inntekten din når du må være borte fra arbeid for å pleie et sykt barn."),
    Triple("omsorgspenger", "Omsorgspenger", "Omsorgspenger kan du få når du må være borte fra jobb fordi barnet ditt er sykt."),
    Triple("opplaeringspenger", "Opplæringspenger", "Opplæringspenger kan du få når du deltar på opplæring for å ta vare på et barn med langvarig sykdom."),
    Triple("tiltakspenger", "Tiltakspenger", "Tiltakspenger kan du få mens du deltar på et arbeidsmarkedstiltak."),
    Triple("lonnsgaranti", "Lønnsgaranti ved konkurs", "Lønnsgarantiordningen sikrer lønn til deg som jobber i en bedrift som går konkurs."),
    Triple("svangerskapspenger", "Svangerskapspenger", "Svangerskapspenger kan du få når du er gravid og ikke kan fortsette i jobben din."),
    Triple("feriepenger", "Feriepenger", "Du opptjener feriepenger av enkelte ytelser fra Nav."),
    Triple("kvalifiseringsprogrammet", "Kvalifiseringsprogrammet", "Kvalifiseringsprogrammet skal hjelpe deg som står langt fra arbeidslivet ut i arbeid."),
    Triple("sosialhjelp", "Økonomisk sosialhjelp", "Økonomisk sosialhjelp er en midlertidig ytelse for deg som ikke kan forsørge deg selv."),
    Triple("arbeidsrettet-rehabilitering", "Arbeidsrettet rehabilitering", "Arbeidsrettet rehabilitering kan hjelpe deg tilbake i arbeid etter sykdom."),
    Triple("jobbsoker", "Tips til jobbsøkere", "Her får du tips om hvordan du søker jobb og skriver en god CV."),
    Triple("arbeidssoker", "Registrer deg som arbeidssøker", "Du må registrere deg som arbeidssøker for å få dagpenger og hjelp til å finne arbeid."),
    Triple("supplerende-stonad", "Supplerende stønad", "Supplerende stønad er for deg som har kort botid i Norge."),
    Triple("bilstonad", "Stønad til bil", "Du kan få stønad til bil hvis du har varig nedsatt funksjonsevne."),
    Triple("tannbehandling", "Tannbehandling", "Nav kan dekke utgifter til tannbehandling i enkelte tilfeller."),
    Triple("yrkesskade", "Yrkesskade", "Hvis du blir skadet i arbeid, kan du ha rett til særfordeler fra Nav."),
    Triple("ventelonn", "Ventelønn", "Ventelønn var en ordning for statsansatte som ble sagt opp."),
    Triple("gravferdsstonad", "Gravferdsstønad", "Gravferdsstønad kan dekke utgifter til gravferd."),
    Triple("midlertidig-bolig", "Midlertidig bolig", "Har du ikke et sted å bo, kan Nav hjelpe deg med midlertidig bolig."),
    Triple("min-side", "Min side", "På Min side ser du saker, meldinger og utbetalinger fra Nav."),
).mapIndexed { i, (path, title, ingress) ->
    doc(
        "side-$path", path, title, ingress,
        "$ingress Les om hvem som kan få det, og hvordan du søker.",
        audience = when (i % 3) {
            0 -> listOf(ValidAudiences.PERSON)
            1 -> listOf(ValidAudiences.OTHER)
            else -> emptyList()
        },
        createdAt = "2023-%02d-%02dT12:00:00.000+01:00".format(i % 12 + 1, i + 1),
    )
}

val goldenDocuments: List<Content> =
    privatperson + arbeidsgiver + samarbeidspartner + presseStatistikkOgAnalyse + sprak + fylker + flereSider
