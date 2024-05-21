package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit

private const val HIGHLIGHT_MAX_LENGTH = 250
private const val CUTOFF_PREFIX = "… "
private const val CUTOFF_POSTFIX = " …"
private const val TABELL = "Tabell"

fun ContentSearchHit.toHighlight(): String {
    if (type == ValidTypes.TABELL.descriptor) return TABELL

    val hasTitleHighlight = highlight.title.isNotEmpty() || highlight.titleNgrams.isNotEmpty()

    fun ingressHighlight() = (highlight.ingressNgrams.firstOrNull() ?: highlight.ingress.firstOrNull())?.truncateIngress()
    fun textHighlight() = highlight.text.firstOrNull()?.truncateText()

    return ingressHighlight() ?: textHighlight().takeUnless { hasTitleHighlight } ?: ingress.truncateIngress()
}

private fun String.truncateText(): String {
    return CUTOFF_PREFIX + take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
}

private fun String.truncateIngress(): String {
    return if (length > HIGHLIGHT_MAX_LENGTH) {
        take(HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
    } else this
}