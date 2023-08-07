package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.model.MultiLangField


val initialTestData = listOf(
    Content(
        "1",
        "https://first.com",
        MultiLangField(english = "First name"),
        MultiLangField(english = "First ingress"),
        MultiLangField(english = "First text"),
        "Privatperson",
        "en"
    ),
    Content(
        "2",
        "https://second.com",
        MultiLangField(english = "Second name"),
        MultiLangField(english = "Second ingress"),
        MultiLangField(english = "Second text"),
        "Privatperson",
        "en"
    ),
    Content(
        "3",
        "https://third.com",
        MultiLangField(english = "Third name"),
        MultiLangField(english = "Third ingress"),
        MultiLangField(english = "Third text"),
        "Privatperson",
        "en"
    ),
    Content(
        "4",
        "https://fourth.com",
        MultiLangField(english = "Fourth name"),
        MultiLangField(english = "Fourth ingress"),
        MultiLangField(english = "Fourth text"),
        "Privatperson",
        "en"
    ),
    Content(
        "5",
        "https://fifth.com",
        MultiLangField(english = "Fifth name"),
        MultiLangField(english = "Fifth ingress"),
        MultiLangField(english = "Fifth text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "6",
        "https://sixth.com",
        MultiLangField(english = "Sixth name"),
        MultiLangField(english = "Sixth ingress"),
        MultiLangField(english = "Sixth text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "7",
        "https://seventh.com",
        MultiLangField(english = "Seventh name"),
        MultiLangField(english = "Seventh ingress"),
        MultiLangField(english = "Seventh text"),
        "Arbeidsgiver",
        "en"
    ),
    Content(
        "8",
        "https://eighth.com",
        MultiLangField(english = "Eighth name"),
        MultiLangField(english = "Eighth ingress"),
        MultiLangField(english = "Eighth text"),
        "Samarbeidspartner",
        "en"
    ),
    Content(
        "9",
        "https://ninth.com",
        MultiLangField(english = "Ninth name"),
        MultiLangField(english = "Ninth ingress"),
        MultiLangField(english = "Ninth text"),
        "Samarbeidspartner",
        "en"
    ),
    Content(
        "10",
        "https://tenth.com",
        MultiLangField(english = "Tenth name"),
        MultiLangField(english = "Tenth ingress"),
        MultiLangField(english = "Tenth text"),
        "Samarbeidspartner",
        "en"
    ),
)

val additionalTestData = listOf(
    Content(
        "11",
        "https://eleventh.com",
        MultiLangField(english = "Eleventh name"),
        MultiLangField(english = "Eleventh ingress"),
        MultiLangField(english = "Eleventh text"),
        "Samarbeidspartner",
        "en"
    )
)