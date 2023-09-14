package no.nav.navnosearchapi.search.dto

import no.nav.navnosearchapi.common.dto.ContentDto

data class ContentSearchHit(val content: ContentDto, val highlight: ContentHighlight)