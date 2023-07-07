package no.nav.navnosearchapi.service.search.mapper

import no.nav.navnosearchapi.dto.ContentSearchHit
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.Content
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    fun toContentSearchPage(searchPage: SearchPage<Content>): ContentSearchPage {
        return ContentSearchPage(
            hits = searchPage.searchHits.searchHits.map { hit -> ContentSearchHit(hit.content) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number
        )
    }
}