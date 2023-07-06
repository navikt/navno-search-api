package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.model.Content

fun initialTestData(): List<Content> {
    return listOf(
        Content("1", "https://first.com", "First name", "First ingress", "First text", "Privatperson"),
        Content("2", "https://second.com", "Second name", "Second ingress", "Second text", "Privatperson"),
        Content("3", "https://third.com", "Third name", "Third ingress", "Third text", "Privatperson"),
        Content("4", "https://fourth.com", "Fourth name", "Fourth ingress", "Fourth text", "Privatperson"),
        Content("5", "https://fifth.com", "Fifth name", "Fifth ingress", "Fifth text", "Arbeidsgiver"),
        Content("6", "https://sixth.com", "Sixth name", "Sixth ingress", "Sixth text", "Arbeidsgiver"),
        Content("7", "https://seventh.com", "Seventh name", "Seventh ingress", "Seventh text", "Arbeidsgiver"),
        Content("8", "https://eighth.com", "Eighth name", "Eighth ingress", "Eighth text", "Samarbeidspartner"),
        Content("9", "https://ninth.com", "Ninth name", "Ninth ingress", "Ninth text", "Samarbeidspartner"),
        Content("10", "https://tenth.com", "Tenth name", "Tenth ingress", "Tenth text", "Samarbeidspartner"),
    )
}