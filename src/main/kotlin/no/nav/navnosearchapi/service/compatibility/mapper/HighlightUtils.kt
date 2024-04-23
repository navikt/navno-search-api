package no.nav.navnosearchapi.service.compatibility.mapper

import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit

private const val HIGHLIGHT_MAX_LENGTH = 250
private const val CUTOFF_PREFIX = "… "
private const val CUTOFF_POSTFIX = " …"
private const val TABELL = "Tabell"

fun ContentSearchHit.toHighlight(): String {
    if (this.type == ValidTypes.TABELL.descriptor) return TABELL

    val highlight = if (this.highlight.let { it.title.isNotEmpty() || it.ingress.isNotEmpty() }) {
        this.highlight.ingress.firstOrNull()?.truncateIngress()
    } else {
        this.highlight.text.firstOrNull()?.truncateText()
    }

    return highlight ?: this.ingress.truncateIngress()
}

private fun String.truncateText(): String {
    val highlightTruncated = this.takeIf { this.length <= HIGHLIGHT_MAX_LENGTH }
        ?: this.substring(0, HIGHLIGHT_MAX_LENGTH)

    return CUTOFF_PREFIX + highlightTruncated + CUTOFF_POSTFIX
}

private fun String.truncateIngress(): String {
    return if (this.length > HIGHLIGHT_MAX_LENGTH) {
        this.substring(0, HIGHLIGHT_MAX_LENGTH) + CUTOFF_POSTFIX
    } else this
}