package no.nav.navnosearchapi.service.search.mapper

import no.nav.navnosearchapi.dto.ContentHighlight
import no.nav.navnosearchapi.dto.ContentSearchHit
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.Content
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    fun toContentSearchPage(searchPage: SearchPage<Content>): ContentSearchPage {
        return ContentSearchPage(
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<Content>): ContentSearchHit {
        return ContentSearchHit(
            content = searchHit.content,
            highlight = ContentHighlight(
                name = searchHit.getHighlightField(NAME),
                ingress = searchHit.getHighlightField(INGRESS),
                text = searchHit.getHighlightField(TEXT),
            ),
        )
    }

    companion object {
        private const val NAME = "name"
        private const val INGRESS = "ingress"
        private const val TEXT = "text"
    }
}