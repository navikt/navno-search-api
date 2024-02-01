package no.nav.navnosearchapi.service.search.mapper

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchadminapi.common.model.MultiLangField
import no.nav.navnosearchapi.service.search.dto.ContentHighlight
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.Aggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    fun toContentSearchPage(
        searchPage: SearchPage<ContentDao>,
        isMatchPhraseQuery: Boolean
    ): ContentSearchPage {
        val aggregations = searchPage.searchHits.aggregations
        return ContentSearchPage(
            suggestions = searchPage.searchHits.suggest?.suggestions?.first()?.entries?.flatMap { entry -> entry.options.map { it.text } },
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it, isMatchPhraseQuery) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
            aggregations = if (aggregations != null) mapAggregations((aggregations as OpenSearchAggregations).aggregations()) else null
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<ContentDao>, isMatchPhraseQuery: Boolean): ContentSearchHit {
        val content = searchHit.content
        return ContentSearchHit(
            href = content.href,
            title = languageSubfieldValue(content.title, content.language),
            ingress = languageSubfieldValue(content.ingress, content.language),
            text = languageSubfieldValue(content.text, content.language),
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

        if (isMatchPhraseQuery) suffix += ".$EXACT_INNER_FIELD"

        return parentKey + suffix
    }

    private fun languageSubfieldValue(field: MultiLangField, language: String): String {
        return when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> field.no ?: ""
            ENGLISH -> field.en ?: ""
            else -> field.other ?: ""
        }
    }

    companion object {
        private const val INGRESS = "ingress"
        private const val TEXT = "text"

        private const val NORWEGIAN_SUFFIX = ".no"
        private const val ENGLISH_SUFFIX = ".en"
        private const val OTHER_SUFFIX = ".other"
    }
}