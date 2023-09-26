package no.nav.navnosearchapi.search.mapper

import no.nav.navnosearchapi.common.mapper.ContentDtoMapper
import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.utils.AUDIENCE
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.common.utils.IS_FILE
import no.nav.navnosearchapi.common.utils.LANGUAGE
import no.nav.navnosearchapi.common.utils.METATAGS
import no.nav.navnosearchapi.common.utils.NORWEGIAN_BOKMAAL
import no.nav.navnosearchapi.common.utils.NORWEGIAN_NYNORSK
import no.nav.navnosearchapi.search.dto.ContentAggregations
import no.nav.navnosearchapi.search.dto.ContentHighlight
import no.nav.navnosearchapi.search.dto.ContentSearchHit
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.search.aggregations.Aggregations
import org.opensearch.search.aggregations.bucket.filter.Filter
import org.opensearch.search.aggregations.bucket.range.Range
import org.opensearch.search.aggregations.bucket.terms.Terms
import org.opensearch.search.aggregations.metrics.Cardinality
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class ContentSearchPageMapper(val contentDtoMapper: ContentDtoMapper) {
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
        val language = searchHit.content.language
        return ContentSearchHit(
            content = contentDtoMapper.toContentDto(searchHit.content),
            highlight = ContentHighlight(
                title = searchHit.getHighlightField(languageSubfieldKey(TITLE, language)),
                ingress = searchHit.getHighlightField(languageSubfieldKey(INGRESS, language)),
                text = searchHit.getHighlightField(languageSubfieldKey(TEXT, language)),
            ),
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

    private fun getCardinalityAggregation(aggregations: Aggregations, name: String): Long {
        return aggregations.get<Cardinality>(name).value
    }

    private fun languageSubfieldKey(parentKey: String, language: String): String {
        val suffix = when (language) {
            NORWEGIAN_BOKMAAL, NORWEGIAN_NYNORSK -> NORWEGIAN_SUFFIX
            ENGLISH -> ENGLISH_SUFFIX
            else -> OTHER_SUFFIX
        }
        return parentKey + suffix
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