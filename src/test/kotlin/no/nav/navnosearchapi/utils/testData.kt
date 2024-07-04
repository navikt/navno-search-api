package no.nav.navnosearchapi.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchadminapi.common.model.MultiLangFieldLong
import no.nav.navnosearchadminapi.common.model.MultiLangFieldShort
import org.springframework.data.elasticsearch.core.suggest.Completion
import java.time.ZonedDateTime

const val TEAM_NAME = "test-team"
const val HINDI = "hi"

val PRIVATPERSON = ValidAudiences.PERSON.descriptor
val ARBEIDSGIVER = ValidAudiences.EMPLOYER.descriptor
val SAMARBEIDSPARTNER = ValidAudiences.PROVIDER.descriptor
val AGDER = ValidFylker.AGDER.descriptor
val STATISTIKK = ValidMetatags.STATISTIKK.descriptor
val INFORMASJON = ValidMetatags.INFORMASJON.descriptor

val now: ZonedDateTime = ZonedDateTime.now()
val nowMinusTwoYears: ZonedDateTime = ZonedDateTime.now().minusYears(2)
val nowMinus10Days: ZonedDateTime = ZonedDateTime.now().minusDays(10)
val nowMinus50Days: ZonedDateTime = ZonedDateTime.now().minusDays(50)

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
        timestamp = nowMinusTwoYears,
        fylke = AGDER,
        metatags = listOf(STATISTIKK),
    ),
    dummyContent(
        externalId = "4",
        textPrefix = "Fourth",
        timestamp = nowMinusTwoYears,
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "5",
        textPrefix = "Fifth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "6",
        textPrefix = "Sixth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "7",
        textPrefix = "Seventh",
        timestamp = nowMinus50Days,
        audience = listOf(ARBEIDSGIVER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "8",
        textPrefix = "Eighth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContent(
        externalId = "9",
        textPrefix = "Ninth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),

    ),
    dummyContent(
        externalId = "10",
        textPrefix = "Tenth",
        timestamp = nowMinus50Days,
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
    timestamp: ZonedDateTime = now,
    audience: List<String> = listOf(PRIVATPERSON),
    language: String = NORWEGIAN_BOKMAAL,
    fylke: String? = null,
    metatags: List<String> = emptyList()
): Content {
    val title = "$textPrefix title"
    val ingress = "$textPrefix ingress"
    val text = "$textPrefix text"
    val allText = listOf(title, ingress, text).joinToString()

    return Content(
        id = "$teamName-$externalId",
        autocomplete = Completion(listOf("$textPrefix title")),
        teamOwnedBy = teamName,
        href = "https://$textPrefix.com",
        title = MultiLangFieldShort(value = title, language = language),
        ingress = MultiLangFieldShort(value = ingress, language = language),
        text = MultiLangFieldLong(value = text, language = language),
        allText = MultiLangFieldLong(value = allText, language = language),
        type = type,
        createdAt = timestamp,
        lastUpdated = timestamp,
        sortByDate = timestamp,
        audience = audience,
        language = language,
        fylke = fylke,
        metatags = metatags
    )
}