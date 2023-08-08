package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField


val initialTestData = listOf(
    ContentDao(
        "1",
        "https://first.com",
        MultiLangField(en = "First name"),
        MultiLangField(en = "First ingress"),
        MultiLangField(en = "First text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "2",
        "https://second.com",
        MultiLangField(en = "Second name"),
        MultiLangField(en = "Second ingress"),
        MultiLangField(en = "Second text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "3",
        "https://third.com",
        MultiLangField(en = "Third name"),
        MultiLangField(en = "Third ingress"),
        MultiLangField(en = "Third text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "4",
        "https://fourth.com",
        MultiLangField(en = "Fourth name"),
        MultiLangField(en = "Fourth ingress"),
        MultiLangField(en = "Fourth text"),
        "Privatperson",
        "en"
    ),
    ContentDao(
        "5",
        "https://fifth.com",
        MultiLangField(en = "Fifth name"),
        MultiLangField(en = "Fifth ingress"),
        MultiLangField(en = "Fifth text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "6",
        "https://sixth.com",
        MultiLangField(en = "Sixth name"),
        MultiLangField(en = "Sixth ingress"),
        MultiLangField(en = "Sixth text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "7",
        "https://seventh.com",
        MultiLangField(en = "Seventh name"),
        MultiLangField(en = "Seventh ingress"),
        MultiLangField(en = "Seventh text"),
        "Arbeidsgiver",
        "en"
    ),
    ContentDao(
        "8",
        "https://eighth.com",
        MultiLangField(en = "Eighth name"),
        MultiLangField(en = "Eighth ingress"),
        MultiLangField(en = "Eighth text"),
        "Samarbeidspartner",
        "en"
    ),
    ContentDao(
        "9",
        "https://ninth.com",
        MultiLangField(en = "Ninth name"),
        MultiLangField(en = "Ninth ingress"),
        MultiLangField(en = "Ninth text"),
        "Samarbeidspartner",
        "en"
    ),
    ContentDao(
        "10",
        "https://tenth.com",
        MultiLangField(en = "Tenth name"),
        MultiLangField(en = "Tenth ingress"),
        MultiLangField(en = "Tenth text"),
        "Samarbeidspartner",
        "en"
    ),
)

val additionalTestData = listOf(
    ContentDao(
        "11",
        "https://eleventh.com",
        MultiLangField(en = "Eleventh name"),
        MultiLangField(en = "Eleventh ingress"),
        MultiLangField(en = "Eleventh text"),
        "Samarbeidspartner",
        "en"
    )
)