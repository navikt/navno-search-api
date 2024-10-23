package no.nav.navnosearchapi.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.Content
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

const val TEAM_NAME = "test-team"
const val HINDI = "hi"

val PRIVATPERSON = ValidAudiences.PERSON.descriptor
val ARBEIDSGIVER = ValidAudiences.EMPLOYER.descriptor
val SAMARBEIDSPARTNER = ValidAudiences.PROVIDER.descriptor
val AGDER = ValidFylker.AGDER.descriptor
val STATISTIKK = ValidMetatags.STATISTIKK.descriptor
val INFORMASJON = ValidMetatags.INFORMASJON.descriptor

val fixedNow: ZonedDateTime = ZonedDateTime.of(
    LocalDateTime.of(2020, 1, 1, 12, 0),
    ZoneId.of("Europe/Oslo")
)
val fixedNowMinusTwoYears: ZonedDateTime = fixedNow.minusYears(2)
val fixedNowMinus10Days: ZonedDateTime = fixedNow.minusDays(10)
val fixedNowMinus50Days: ZonedDateTime = fixedNow.minusDays(50)

val initialTestData = listOf(
    dummyContent(
        externalId = "1",
        textPrefix = "First",
        audience = listOf(PRIVATPERSON, ARBEIDSGIVER, SAMARBEIDSPARTNER),
        type = ValidTypes.TABELL.descriptor,
        fylke = AGDER,
    ),
    dummyContent(
        externalId = "2",
        textPrefix = "Second",
        fylke = AGDER,
        metatags = listOf(STATISTIKK),
    ),
    dummyContent(
        externalId = "3",
        textPrefix = "Third",
        timestamp = fixedNowMinusTwoYears,
        fylke = AGDER,
        metatags = listOf(STATISTIKK),
    ),
    dummyContent(
        externalId = "4",
        textPrefix = "Fourth",
        timestamp = fixedNowMinusTwoYears,
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "5",
        textPrefix = "Fifth",
        timestamp = fixedNowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "6",
        textPrefix = "Sixth",
        timestamp = fixedNowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "7",
        textPrefix = "Seventh",
        timestamp = fixedNowMinus50Days,
        audience = listOf(ARBEIDSGIVER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "8",
        textPrefix = "Eighth",
        timestamp = fixedNowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "9",
        textPrefix = "Ninth",
        timestamp = fixedNowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),

    ),
    dummyContent(
        externalId = "10",
        textPrefix = "Tenth",
        timestamp = fixedNowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
)

fun dummyContent(
    teamName: String = TEAM_NAME,
    externalId: String,
    textPrefix: String,
    type: String = ValidTypes.ANDRE.descriptor,
    timestamp: ZonedDateTime = fixedNow,
    audience: List<String> = listOf(PRIVATPERSON),
    language: String = NORWEGIAN_BOKMAAL,
    fylke: String? = null,
    metatags: List<String> = emptyList()
): Content {
    val title = "$textPrefix title"
    val ingress = "$textPrefix ingress"
    val text = "$textPrefix text"

    return Content.from(
        id = "$teamName-$externalId",
        teamOwnedBy = teamName,
        href = "https://$textPrefix.com",
        title = title,
        ingress = ingress,
        text = text,
        type = type,
        createdAt = timestamp,
        lastUpdated = timestamp,
        sortByDate = timestamp,
        audience = audience,
        language = language,
        fylke = fylke,
        metatags = metatags,
    )
}