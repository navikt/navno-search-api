package no.nav.navnosearchapi.utils

import no.nav.navnosearchapi.exception.DocumentForTeamNameNotFoundException

fun createId(teamName: String, externalId: String) = "$teamName-$externalId"

fun extractExternalId(id: String, teamName: String): String {
    val expectedPrefix = "$teamName-"
    if (id.startsWith(expectedPrefix)) {
        return id.substring(expectedPrefix.length)
    } else {
        throw DocumentForTeamNameNotFoundException("Feil ved utledning av ekstern id - dokument med id $id tilhører ikke team $teamName")
    }
}