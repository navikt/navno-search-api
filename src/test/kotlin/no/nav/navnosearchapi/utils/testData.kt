package no.nav.navnosearchapi.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchadminapi.common.model.MultiLangField
import org.springframework.data.elasticsearch.core.suggest.Completion
import java.time.ZonedDateTime

const val TEAM_NAME = "test-team"
const val PRIVATPERSON = "privatperson"
const val ARBEIDSGIVER = "arbeidsgiver"
const val SAMARBEIDSPARTNER = "samarbeidspartner"
const val AGDER = "agder"
const val STATISTIKK = "statistikk"
const val INFORMASJON = "informasjon"
const val HINDI = "hi"

val now: ZonedDateTime = ZonedDateTime.now()
val nowMinusTwoYears: ZonedDateTime = ZonedDateTime.now().minusYears(2)
val nowMinus10Days: ZonedDateTime = ZonedDateTime.now().minusDays(10)
val nowMinus50Days: ZonedDateTime = ZonedDateTime.now().minusDays(50)

val initialTestData = listOf(
    dummyContentDao(
        externalId = "1",
        textPrefix = "First",
        audience = listOf(PRIVATPERSON, ARBEIDSGIVER, SAMARBEIDSPARTNER),
        type = ValidTypes.FIL_DOCUMENT.descriptor,
        fylke = AGDER,
    ),
    dummyContentDao(
        externalId = "2",
        textPrefix = "Second",
        fylke = AGDER,
        metatags = listOf(STATISTIKK),
    ),
    dummyContentDao(
        externalId = "3",
        textPrefix = "Third",
        timestamp = nowMinusTwoYears,
        fylke = AGDER,
        metatags = listOf(STATISTIKK),
    ),
    dummyContentDao(
        externalId = "4",
        textPrefix = "Fourth",
        timestamp = nowMinusTwoYears,
        language = ENGLISH,
        metatags = listOf(INFORMASJON),

        ),
    dummyContentDao(
        externalId = "5",
        textPrefix = "Fifth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContentDao(
        externalId = "6",
        textPrefix = "Sixth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
        metatags = listOf(INFORMASJON),
    ),
    dummyContentDao(
        externalId = "7",
        textPrefix = "Seventh",
        timestamp = nowMinus50Days,
        audience = listOf(ARBEIDSGIVER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContentDao(
        externalId = "8",
        textPrefix = "Eighth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
    dummyContentDao(
        externalId = "9",
        textPrefix = "Ninth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),

        ),
    dummyContentDao(
        externalId = "10",
        textPrefix = "Tenth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
        metatags = listOf(INFORMASJON),
    ),
)

fun dummyContentDao(
    teamName: String = TEAM_NAME,
    externalId: String,
    textPrefix: String,
    type: String = ValidTypes.ANDRE.descriptor,
    timestamp: ZonedDateTime = now,
    audience: List<String> = listOf(PRIVATPERSON),
    language: String = NORWEGIAN_BOKMAAL,
    fylke: String? = null,
    metatags: List<String> = emptyList()
): ContentDao {
    val title = "$textPrefix title"
    val ingress = "$textPrefix ingress"
    val text = "$textPrefix text"

    return ContentDao(
        id = "$teamName-$externalId",
        autocomplete = Completion(listOf("$textPrefix title")),
        teamOwnedBy = teamName,
        href = "https://$textPrefix.com",
        title = MultiLangField(values = listOf(title), language = language),
        ingress = MultiLangField(values = listOf(ingress), language = language),
        text = MultiLangField(values = listOf(text), language = language),
        titleWithSynonyms = MultiLangField(values = listOf(title), language = language),
        ingressWithSynonyms = MultiLangField(values = listOf(ingress), language = language),
        allText = MultiLangField(values = listOf(title, ingress, text), language = language),
        type = type,
        createdAt = timestamp,
        lastUpdated = timestamp,
        audience = audience,
        language = language,
        fylke = fylke,
        metatags = metatags
    )
}