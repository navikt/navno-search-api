package no.nav.navnosearchapi.dto

import no.nav.navnosearchapi.model.Content

// Todo: add highlight fields etc.
data class ContentSearchHit(val content: Content, val highlight: ContentHighlight)