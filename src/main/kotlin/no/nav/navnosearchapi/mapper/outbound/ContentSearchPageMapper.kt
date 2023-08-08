package no.nav.navnosearchapi.mapper.outbound

import no.nav.navnosearchapi.dto.ContentHighlight
import no.nav.navnosearchapi.dto.ContentSearchHit
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.NORWEGIAN
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component
import no.nav.navnosearchapi.dto.Content as ContentDto

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
        val language = searchHit.content.language
        return ContentSearchHit(
            content = toContentDto(searchHit.content),
            highlight = ContentHighlight(
                name = searchHit.getHighlightField(languageSubfieldKey(NAME, language)),
                ingress = searchHit.getHighlightField(languageSubfieldKey(INGRESS, language)),
                text = searchHit.getHighlightField(languageSubfieldKey(TEXT, language)),
            ),
        )
    }

    private fun toContentDto(content: Content): ContentDto {
        return ContentDto(
            id = content.id,
            href = content.href,
            name = languageSubfieldValue(content.name, content.language),
            ingress = languageSubfieldValue(content.ingress, content.language),
            text = languageSubfieldValue(content.text, content.language),
            maalgruppe = content.maalgruppe,
            language = content.language,
        )
    }

    private fun languageSubfieldKey(parentKey: String, language: String) = "$parentKey.$language"

    private fun languageSubfieldValue(field: MultiLangField, language: String) =
        (if (NORWEGIAN == language) field.no else field.en) ?: ""

    companion object {
        private const val NAME = "name"
        private const val INGRESS = "ingress"
        private const val TEXT = "text"
    }
}