package no.nav.navnosearchapi.search.search.dto

import no.nav.navnosearchapi.common.dto.ContentDto

data class ContentSearchHit(val content: ContentDto, val highlight: ContentHighlight)