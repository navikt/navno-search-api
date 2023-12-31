package no.nav.navnosearchapi.service.search.mapper

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchadminapi.common.model.MultiLangField
import no.nav.navnosearchapi.service.search.dto.ContentHighlight
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.service.search.queries.EXACT_INNER_FIELD_PATH
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.Aggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    val logger: Logger = LoggerFactory.getLogger(ContentSearchPageMapper::class.java)

    fun toContentSearchPage(
        searchPage: SearchPage<ContentDao>,
        mapCustomAggregations: Boolean,
        isMatchPhraseQuery: Boolean
    ): ContentSearchPage {
        return ContentSearchPage(
            suggestions = searchPage.searchHits.suggest?.suggestions?.first()?.entries?.flatMap { entry -> entry.options.map { it.text } },
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it, isMatchPhraseQuery) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
            aggregations = mapAggregations((searchPage.searchHits.aggregations as OpenSearchAggregations).aggregations())
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<ContentDao>, isMatchPhraseQuery: Boolean): ContentSearchHit {
        val content = searchHit.content
        return ContentSearchHit(
            href = content.href,
            title = languageSubfieldValue(content.title, content.language)
                ?: handleMissingValue(content.id, TITLE),
            ingress = languageSubfieldValue(content.ingress, content.language)
                ?: handleMissingValue(content.id, INGRESS),
            text = languageSubfieldValue(content.text, content.language)
                ?: handleMissingValue(content.id, TEXT),
            audience = content.audience,
            language = content.language,
            lastUpdated = content.lastUpdated,
            highlight = ContentHighlight(
                ingress = searchHit.getHighlightField(
                    languageSubfieldKey(INGRESS, content.language, isMatchPhraseQuery)
                ),
                text = searchHit.getHighlightField(
                    languageSubfieldKey(TEXT, content.language, isMatchPhraseQuery)
                ),
            ),
            type = searchHit.content.type,
            score = searchHit.score
        )
    }

    private fun mapAggregations(aggregations: Aggregations): Map<String, Long> {
        return aggregations.associate { it.name to (it as Filter).docCount }
    }

    private fun languageSubfieldKey(parentKey: String, language: String, isMatchPhraseQuery: Boolean): String {
        var suffix = when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> NORWEGIAN_SUFFIX
            ENGLISH -> ENGLISH_SUFFIX
            else -> OTHER_SUFFIX
        }

        if (isMatchPhraseQuery) suffix += EXACT_INNER_FIELD_PATH

        return parentKey + suffix
    }

    private fun languageSubfieldValue(field: MultiLangField, language: String): String? {
        return when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> field.no
            ENGLISH -> field.en
            else -> field.other
        }
    }

    private fun handleMissingValue(id: String, field: String): String {
        logger.warn("Mapping av felt $field feilet for dokument med id $id. Returnerer tom string.")
        return ""
    }

    companion object {
        private const val TITLE = "title"
        private const val INGRESS = "ingress"
        private const val TEXT = "text"

        private const val NORWEGIAN_SUFFIX = ".no"
        private const val ENGLISH_SUFFIX = ".en"
        private const val OTHER_SUFFIX = ".other"
    }
}