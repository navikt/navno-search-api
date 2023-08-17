package no.nav.navnosearchapi.mapper.outbound

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.dto.ContentHighlight
import no.nav.navnosearchapi.dto.ContentSearchHit
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.NORWEGIAN
import no.nav.navnosearchapi.utils.extractExternalId
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    fun toContentSearchPage(searchPage: SearchPage<ContentDao>, suggestions: List<String?>): ContentSearchPage {
        return ContentSearchPage(
            suggestions = suggestions,
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
        )
    }

    fun toContentDto(content: ContentDao): ContentDto {
        return ContentDto(
            id = extractExternalId(content.id, content.teamOwnedBy),
            href = content.href,
            name = languageSubfieldValue(content.name, content.language),
            ingress = languageSubfieldValue(content.ingress, content.language),
            text = languageSubfieldValue(content.text, content.language),
            maalgruppe = content.maalgruppe,
            language = content.language,
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<ContentDao>): ContentSearchHit {
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

    private fun languageSubfieldKey(parentKey: String, language: String) = "$parentKey.$language"

    private fun languageSubfieldValue(field: MultiLangField, language: String) =
        (if (NORWEGIAN == language) field.no else field.en) ?: ""

    companion object {
        private const val NAME = "name"
        private const val INGRESS = "ingress"
        private const val TEXT = "text"
    }
}