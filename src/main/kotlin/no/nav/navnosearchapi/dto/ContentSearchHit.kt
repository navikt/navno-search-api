package no.nav.navnosearchapi.dto

import no.nav.navnosearchapi.model.Content

data class ContentSearchHit(val content: Content, val highlight: ContentHighlight)