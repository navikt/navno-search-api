package no.nav.navnosearchapi.mapper.outbound

import no.nav.navnosearchapi.dto.ContentAggregations
import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.dto.ContentHighlight
import no.nav.navnosearchapi.dto.ContentMetadata
import no.nav.navnosearchapi.dto.ContentSearchHit
import no.nav.navnosearchapi.dto.ContentSearchPage
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.AUDIENCE
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.NORWEGIAN
import no.nav.navnosearchapi.utils.extractExternalId
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.Aggregations
import org.opensearch.search.aggregations.bucket.terms.Terms
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {

    val logger: Logger = LoggerFactory.getLogger(ContentSearchPageMapper::class.java)

    fun toContentSearchPage(searchPage: SearchPage<ContentDao>, suggestions: List<String?>): ContentSearchPage {
        return ContentSearchPage(
            suggestions = suggestions,
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
            aggregations = toContentAggregations(searchPage.searchHits.aggregations as OpenSearchAggregations)
        )
    }

    fun toContentDto(content: ContentDao): ContentDto {
        return ContentDto(
            id = extractExternalId(content.id, content.teamOwnedBy),
            href = content.href,
            title = languageSubfieldValue(content.title, content.language)
                ?: handleMissingValue(content.id, TITLE),
            ingress = languageSubfieldValue(content.ingress, content.language)
                ?: handleMissingValue(content.id, INGRESS),
            text = languageSubfieldValue(content.text, content.language)
                ?: handleMissingValue(content.id, TEXT),
            ContentMetadata(
                audience = content.audience,
                language = content.language,
                isFile = content.isFile,
                fylke = content.fylke,
                metatags = content.metatags,
            )
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<ContentDao>): ContentSearchHit {
        val language = searchHit.content.language
        return ContentSearchHit(
            content = toContentDto(searchHit.content),
            highlight = ContentHighlight(
                title = searchHit.getHighlightField(languageSubfieldKey(TITLE, language)),
                ingress = searchHit.getHighlightField(languageSubfieldKey(INGRESS, language)),
                text = searchHit.getHighlightField(languageSubfieldKey(TEXT, language)),
            ),
        )
    }

    private fun toContentAggregations(aggregations: OpenSearchAggregations): ContentAggregations {
        return ContentAggregations(audience = getAggregations(aggregations.aggregations(), AUDIENCE))
    }

    private fun getAggregations(aggregations: Aggregations, name: String): Map<String, Long> {
        return aggregations.get<Terms>(name).buckets.associate { it.keyAsString to it.docCount }
    }

    private fun languageSubfieldKey(parentKey: String, language: String) = "$parentKey.$language"

    private fun languageSubfieldValue(field: MultiLangField, language: String): String? {
        return when (language) {
            NORWEGIAN -> field.no
            ENGLISH -> field.en
            else -> field.other
        }
    }

    private fun handleMissingValue(id: String, field: String): String {
        logger.warn("Mapping av felt $field feilet for dokument ned id $id. Returnerer tom string.")
        return ""
    }

    companion object {
        private const val TITLE = "title"
        private const val INGRESS = "ingress"
        private const val TEXT = "text"
    }
}