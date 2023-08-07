package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.model.MultiLangField


val initialTestData = listOf(
    Content(
        "1",
        "https://first.com",
        MultiLangField(en = "First name"),
        MultiLangField(en = "First ingress"),
        MultiLangField(en = "First text"),
        "Privatperson",
        "en"
    ),
    Content(
        "2",
        "https://second.com",
        MultiLangField(en = "Second name"),
        MultiLangField(en = "Second ingress"),
        MultiLangField(en = "Second text"),
        "Privatperson",
        "en"
    ),
    Content(
        "3",
        "https://third.com",
        MultiLangField(en = "Third name"),
        MultiLangField(en = "Third ingress"),
        MultiLangField(en = "Third text"),
        "Privatperson",
        "en"
    ),
    Content(
        "4",
        "https://fourth.com",
        MultiLangField(en = "Fourth name"),
        MultiLangField(en = "Fourth ingress"),
        MultiLangField(en = "Fourth text"),
        "Privatperson",
        "en"
    ),
    Content(
        "5",
        "https://fifth.com",
        MultiLangField(en = "Fifth name"),
        MultiLangField(en = "Fifth ingress"),
        MultiLangField(en = "Fifth text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "6",
        "https://sixth.com",
        MultiLangField(en = "Sixth name"),
        MultiLangField(en = "Sixth ingress"),
        MultiLangField(en = "Sixth text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "7",
        "https://seventh.com",
        MultiLangField(en = "Seventh name"),
        MultiLangField(en = "Seventh ingress"),
        MultiLangField(en = "Seventh text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "8",
        "https://eighth.com",
        MultiLangField(en = "Eighth name"),
        MultiLangField(en = "Eighth ingress"),
        MultiLangField(en = "Eighth text"),
        "Samarbeidspartner",
        "en"
    ),
    Content(
        "9",
        "https://ninth.com",
        MultiLangField(en = "Ninth name"),
        MultiLangField(en = "Ninth ingress"),
        MultiLangField(en = "Ninth text"),
        "Samarbeidspartner",
        "en"
    ),
    Content(
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
    Content(
        "11",
        "https://eleventh.com",
        MultiLangField(en = "Eleventh name"),
        MultiLangField(en = "Eleventh ingress"),
        MultiLangField(en = "Eleventh text"),
        "Samarbeidspartner",
        "en"
    )
)