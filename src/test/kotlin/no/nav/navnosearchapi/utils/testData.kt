package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.admin.consumer.kodeverk.dto.KodeverkResponse
import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.dto.ContentMetadata
import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.model.MultiLangField
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.NORWEGIAN_BOKMAAL
import org.springframework.data.elasticsearch.core.suggest.Completion
import java.time.LocalDateTime
import java.time.ZonedDateTime

const val TEAM_NAME = "test-team"
const val PRIVATPERSON = "privatperson"
const val ARBEIDSGIVER = "arbeidsgiver"
const val SAMARBEIDSPARTNER = "samarbeidspartner"
const val AGDER = "agder"
const val STATISTIKK = "statistikk"
const val HINDI = "hi"

val mockedKodeverkResponse = KodeverkResponse(listOf("NB", "NN", "EN", "SE", "PL", "UK", "RU"))

val now: ZonedDateTime = ZonedDateTime.now()
val nowMinusTwoYears: ZonedDateTime = ZonedDateTime.now().minusYears(2)
val nowMinus10Days: ZonedDateTime = ZonedDateTime.now().minusDays(10)
val nowMinus50Days: ZonedDateTime = ZonedDateTime.now().minusDays(50)

val initialTestData = listOf(
    dummyContentDao(
        externalId = "1",
        textPrefix = "First",
        audience = listOf(PRIVATPERSON, ARBEIDSGIVER, SAMARBEIDSPARTNER),
        isFile = true,
        fylke = AGDER,
        metatags = listOf(STATISTIKK)
    ),
    dummyContentDao(
        externalId = "2",
        textPrefix = "Second",
        isFile = true,
        fylke = AGDER,
        metatags = listOf(STATISTIKK)
    ),
    dummyContentDao(
        externalId = "3",
        textPrefix = "Third",
        timestamp = nowMinusTwoYears,
        isFile = true,
        fylke = AGDER,
        metatags = listOf(STATISTIKK)
    ),
    dummyContentDao(
        externalId = "4",
        textPrefix = "Fourth",
        timestamp = nowMinusTwoYears,
        language = ENGLISH
    ),
    dummyContentDao(
        externalId = "5",
        textPrefix = "Fifth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
    ),
    dummyContentDao(
        externalId = "6",
        textPrefix = "Sixth",
        timestamp = nowMinus10Days,
        audience = listOf(ARBEIDSGIVER),
        language = ENGLISH,
    ),
    dummyContentDao(
        externalId = "7",
        textPrefix = "Seventh",
        timestamp = nowMinus50Days,
        audience = listOf(ARBEIDSGIVER),
        language = HINDI,
    ),
    dummyContentDao(
        externalId = "8",
        textPrefix = "Eighth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
    ),
    dummyContentDao(
        externalId = "9",
        textPrefix = "Ninth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
    ),
    dummyContentDao(
        externalId = "10",
        textPrefix = "Tenth",
        timestamp = nowMinus50Days,
        audience = listOf(SAMARBEIDSPARTNER),
        language = HINDI,
    ),
)

val additionalTestData = listOf(dummyContentDto())

val additionalTestDataAsMapWithMissingIngress = listOf(
    mapOf(
        "id" to "11",
        "href" to "https://eleventh.com",
        "title" to "Eleventh title",
        "text" to "Eleventh text",
        "audience" to listOf(SAMARBEIDSPARTNER),
        "language" to ENGLISH
    )
)

fun dummyContentDao(
    teamName: String = TEAM_NAME,
    externalId: String,
    textPrefix: String,
    timestamp: ZonedDateTime = now,
    audience: List<String> = listOf(PRIVATPERSON),
    language: String = NORWEGIAN_BOKMAAL,
    isFile: Boolean? = null,
    fylke: String? = null,
    metatags: List<String>? = null
): ContentDao {
    return ContentDao(
        "$teamName-$externalId",
        Completion(listOf("$textPrefix title")),
        teamName,
        "https://$textPrefix.com",
        MultiLangField(value = "$textPrefix title", language = language),
        MultiLangField(value = "$textPrefix ingress", language = language),
        MultiLangField(value = "$textPrefix text", language = language),
        timestamp,
        timestamp,
        audience,
        language,
        isFile,
        fylke,
        metatags
    )
}

fun dummyContentDto(
    id: String = "11",
    href: String = "https://eleventh.com",
    title: String = "Eleventh title",
    ingress: String = "Eleventh ingress",
    text: String = "Eleventh text",
    createdAt: LocalDateTime = now.toLocalDateTime(),
    lastUpdated: LocalDateTime = now.toLocalDateTime(),
    audience: List<String> = listOf(SAMARBEIDSPARTNER),
    language: String = ENGLISH,
    isFile: Boolean? = null,
    fylke: String? = null,
    metatags: List<String>? = null,
) = ContentDto(
    id,
    href,
    title,
    ingress,
    text,
    ContentMetadata(
        createdAt,
        lastUpdated,
        audience,
        language,
        isFile,
        fylke,
        metatags,
    )
)
