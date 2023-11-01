package no.nav.navnosearchapi.service.search.mapper

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.IS_FILE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchadminapi.common.model.MultiLangField
import no.nav.navnosearchapi.service.search.dto.ContentAggregations
import no.nav.navnosearchapi.service.search.dto.ContentHighlight
import no.nav.navnosearchapi.service.search.dto.ContentSearchHit
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.Aggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.opensearch.search.aggregations.bucket.range.Range
import org.opensearch.search.aggregations.bucket.terms.Terms
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper {
    val logger: Logger = LoggerFactory.getLogger(ContentSearchPageMapper::class.java)

    fun toContentSearchPage(searchPage: SearchPage<ContentDao>, mapCustomAggregations: Boolean): ContentSearchPage {
        return ContentSearchPage(
            suggestions = searchPage.searchHits.suggest?.suggestions?.first()?.entries?.flatMap { entry -> entry.options.map { it.text } },
            hits = searchPage.searchHits.searchHits.map { toContentSearchHit(it) },
            totalPages = searchPage.totalPages,
            totalElements = searchPage.totalElements,
            pageSize = searchPage.size,
            pageNumber = searchPage.number,
            aggregations = if (mapCustomAggregations) {
                toCustomAggregations((searchPage.searchHits.aggregations as OpenSearchAggregations).aggregations())
            } else {
                toContentAggregations((searchPage.searchHits.aggregations as OpenSearchAggregations).aggregations())
            }
        )
    }

    private fun toContentSearchHit(searchHit: SearchHit<ContentDao>): ContentSearchHit {
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
                title = searchHit.getHighlightField(languageSubfieldKey(TITLE, content.language)),
                ingress = searchHit.getHighlightField(languageSubfieldKey(INGRESS, content.language)),
                text = searchHit.getHighlightField(languageSubfieldKey(TEXT, content.language)),
            ),
            isKontor = searchHit.content.metatags.contains(ValidMetatags.KONTOR.descriptor)
        )
    }

    private fun toContentAggregations(aggregations: Aggregations): ContentAggregations {
        return ContentAggregations(
            audience = getTermAggregation(aggregations, AUDIENCE),
            language = getTermAggregation(aggregations, LANGUAGE),
            fylke = getTermAggregation(aggregations, FYLKE),
            metatags = getTermAggregation(aggregations, METATAGS),
            isFile = getFilterAggregation(aggregations, IS_FILE),
            dateRangeAggregations = mapOf(
                getDateRangeAggregation(aggregations, DATE_RANGE_LAST_7_DAYS),
                getDateRangeAggregation(aggregations, DATE_RANGE_LAST_30_DAYS),
                getDateRangeAggregation(aggregations, DATE_RANGE_LAST_12_MONTHS),
                getDateRangeAggregation(aggregations, DATE_RANGE_OLDER_THAN_12_MONTHS),
            ),
        )
    }

    private fun toCustomAggregations(aggregations: Aggregations): ContentAggregations {
        return ContentAggregations(custom = aggregations.associate { it.name to (it as Filter).docCount })
    }

    private fun getTermAggregation(aggregations: Aggregations, name: String): Map<String, Long> {
        return aggregations.get<Terms>(name).buckets.associate { it.keyAsString to it.docCount }
    }

    private fun getDateRangeAggregation(aggregations: Aggregations, name: String): Pair<String, Long> {
        return aggregations.get<Range>(name).let { it.name to it.buckets.first().docCount }
    }

    private fun getFilterAggregation(aggregations: Aggregations, name: String): Long {
        return aggregations.get<Filter>(name).docCount
    }

    private fun languageSubfieldKey(parentKey: String, language: String): String {
        val suffix = when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> NORWEGIAN_SUFFIX
            ENGLISH -> ENGLISH_SUFFIX
            else -> OTHER_SUFFIX
        }
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