package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField


val initialTestData = listOf(
    ContentDao(
        "testTeam-1",
        "testTeam",
        "https://first.com",
        MultiLangField(en = "First name"),
        MultiLangField(en = "First ingress"),
        MultiLangField(en = "First text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "testTeam-2",
        "testTeam",
        "https://second.com",
        MultiLangField(en = "Second name"),
        MultiLangField(en = "Second ingress"),
        MultiLangField(en = "Second text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "testTeam-3",
        "testTeam",
        "https://third.com",
        MultiLangField(en = "Third name"),
        MultiLangField(en = "Third ingress"),
        MultiLangField(en = "Third text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "testTeam-4",
        "testTeam",
        "https://fourth.com",
        MultiLangField(en = "Fourth name"),
        MultiLangField(en = "Fourth ingress"),
        MultiLangField(en = "Fourth text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "testTeam-5",
        "testTeam",
        "https://fifth.com",
        MultiLangField(en = "Fifth name"),
        MultiLangField(en = "Fifth ingress"),
        MultiLangField(en = "Fifth text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "testTeam-6",
        "testTeam",
        "https://sixth.com",
        MultiLangField(en = "Sixth name"),
        MultiLangField(en = "Sixth ingress"),
        MultiLangField(en = "Sixth text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "testTeam-7",
        "testTeam",
        "https://seventh.com",
        MultiLangField(en = "Seventh name"),
        MultiLangField(en = "Seventh ingress"),
        MultiLangField(en = "Seventh text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "testTeam-8",
        "testTeam",
        "https://eighth.com",
        MultiLangField(en = "Eighth name"),
        MultiLangField(en = "Eighth ingress"),
        MultiLangField(en = "Eighth text"),
        "Samarbeidspartner",
        "en"
    ),
    ContentDao(
        "testTeam-9",
        "testTeam",
        "https://ninth.com",
        MultiLangField(en = "Ninth name"),
        MultiLangField(en = "Ninth ingress"),
        MultiLangField(en = "Ninth text"),
        "Samarbeidspartner",
        "en"
    ),
    ContentDao(
        "testTeam-10",
        "testTeam",
        "https://tenth.com",
        MultiLangField(en = "Tenth name"),
        MultiLangField(en = "Tenth ingress"),
        MultiLangField(en = "Tenth text"),
        "Samarbeidspartner",
        "en"
    ),
)

val additionalTestData = listOf(
    ContentDto(
        "11",
        "https://eleventh.com",
        "Eleventh name",
        "Eleventh ingress",
        "Eleventh text",
        "Samarbeidspartner",
        "en"
    )
)

val additionalTestDataAsMapWithMissingIngress = listOf(
    mapOf(
        "id" to "11",
        "href" to "https://eleventh.com",
        "name" to "Eleventh name",
        "text" to "Eleventh text",
        "maalgruppe" to "Samarbeidspartner",
        "language" to "en"
    )
)