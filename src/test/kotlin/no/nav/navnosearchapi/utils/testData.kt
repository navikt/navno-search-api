package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.dto.ContentMetadata
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField
import java.time.LocalDateTime
import java.time.ZonedDateTime

const val TEAM_NAME = "test-team"
const val PRIVATPERSON = "privatperson"
const val ARBEIDSGIVER = "arbeidsgiver"
const val SAMARBEIDSPARTNER = "samarbeidspartner"
const val AGDER = "agder"
const val STATISTIKK = "statistikk"

val now: ZonedDateTime = ZonedDateTime.now()
val nowMinusTwoYears: ZonedDateTime = ZonedDateTime.now().minusYears(2)
val nowMinus10Days: ZonedDateTime = ZonedDateTime.now().minusDays(10)
val nowMinus50Days: ZonedDateTime = ZonedDateTime.now().minusDays(50)

val initialTestData = listOf(
    ContentDao(
        "$TEAM_NAME-1",
        TEAM_NAME,
        "https://first.com",
        "First title",
        MultiLangField(no = "First title"),
        MultiLangField(no = "First ingress"),
        MultiLangField(no = "First text"),
        now,
        now,
        listOf(PRIVATPERSON, ARBEIDSGIVER, SAMARBEIDSPARTNER),
        NORWEGIAN_BOKMAAL,
        true,
        AGDER,
        listOf(STATISTIKK)
    ),
    ContentDao(
        "$TEAM_NAME-2",
        TEAM_NAME,
        "https://second.com",
        "Second title",
        MultiLangField(no = "Second title"),
        MultiLangField(no = "Second ingress"),
        MultiLangField(no = "Second text"),
        now,
        now,
        listOf(PRIVATPERSON),
        NORWEGIAN_BOKMAAL,
        true,
        AGDER,
        listOf(STATISTIKK)
    ),
    ContentDao(
        "$TEAM_NAME-3",
        TEAM_NAME,
        "https://third.com",
        "Third title",
        MultiLangField(no = "Third title"),
        MultiLangField(no = "Third ingress"),
        MultiLangField(no = "Third text"),
        nowMinusTwoYears,
        nowMinusTwoYears,
        listOf(PRIVATPERSON),
        NORWEGIAN_BOKMAAL,
        true,
        AGDER,
        listOf(STATISTIKK)
    ),
    ContentDao(
        "$TEAM_NAME-4",
        TEAM_NAME,
        "https://fourth.com",
        "Fourth title",
        MultiLangField(en = "Fourth title"),
        MultiLangField(en = "Fourth ingress"),
        MultiLangField(en = "Fourth text"),
        nowMinusTwoYears,
        nowMinusTwoYears,
        listOf(PRIVATPERSON),
        ENGLISH
    ),
    ContentDao(
        "$TEAM_NAME-5",
        TEAM_NAME,
        "https://fifth.com",
        "Fifth title",
        MultiLangField(en = "Fifth title"),
        MultiLangField(en = "Fifth ingress"),
        MultiLangField(en = "Fifth text"),
        nowMinus10Days,
        nowMinus10Days,
        listOf(ARBEIDSGIVER),
        ENGLISH
    ),
    ContentDao(
        "$TEAM_NAME-6",
        TEAM_NAME,
        "https://sixth.com",
        "Sixth title",
        MultiLangField(en = "Sixth title"),
        MultiLangField(en = "Sixth ingress"),
        MultiLangField(en = "Sixth text"),
        nowMinus10Days,
        nowMinus10Days,
        listOf(ARBEIDSGIVER),
        ENGLISH
    ),
    ContentDao(
        "$TEAM_NAME-7",
        TEAM_NAME,
        "https://seventh.com",
        "Seventh title",
        MultiLangField(other = "Seventh title"),
        MultiLangField(other = "Seventh ingress"),
        MultiLangField(other = "Seventh text"),
        nowMinus50Days,
        nowMinus50Days,
        listOf(ARBEIDSGIVER),
        OTHER
    ),
    ContentDao(
        "$TEAM_NAME-8",
        TEAM_NAME,
        "https://eighth.com",
        "Eighth title",
        MultiLangField(other = "Eighth title"),
        MultiLangField(other = "Eighth ingress"),
        MultiLangField(other = "Eighth text"),
        nowMinus50Days,
        nowMinus50Days,
        listOf(SAMARBEIDSPARTNER),
        OTHER
    ),
    ContentDao(
        "$TEAM_NAME-9",
        TEAM_NAME,
        "https://ninth.com",
        "Ninth title",
        MultiLangField(other = "Ninth title"),
        MultiLangField(other = "Ninth ingress"),
        MultiLangField(other = "Ninth text"),
        nowMinus50Days,
        nowMinus50Days,
        listOf(SAMARBEIDSPARTNER),
        OTHER
    ),
    ContentDao(
        "$TEAM_NAME-10",
        TEAM_NAME,
        "https://tenth.com",
        "Tenth title",
        MultiLangField(other = "Tenth title"),
        MultiLangField(other = "Tenth ingress"),
        MultiLangField(other = "Tenth text"),
        nowMinus50Days,
        nowMinus50Days,
        listOf(SAMARBEIDSPARTNER),
        OTHER
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