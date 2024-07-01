package no.nav.navnosearchapi.client

import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.model.ContentDao
import no.nav.navnosearchapi.client.config.metatagToWeight
import no.nav.navnosearchapi.client.config.termsToOverride
import no.nav.navnosearchapi.client.config.typeToWeight
import no.nav.navnosearchapi.client.dto.ContentSearchPage
import no.nav.navnosearchapi.client.mapper.ContentSearchPageMapper
import no.nav.navnosearchapi.client.queries.highlightBuilder
import no.nav.navnosearchapi.client.queries.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.client.queries.searchAllTextQuery
import no.nav.navnosearchapi.client.queries.searchUrlQuery
import no.nav.navnosearchapi.client.utils.applyFilters
import no.nav.navnosearchapi.client.utils.applyWeighting
import org.opensearch.data.client.orhlc.NativeSearchQuery
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Component

@Component
class SearchClient(
    val operations: ElasticsearchOperations,
    val mapper: ContentSearchPageMapper,
) {
    fun search(
        term: String,
        pageSize: Int,
        page: Int,
        filters: BoolQueryBuilder,
        preAggregationFilters: BoolQueryBuilder? = null,
        aggregations: List<AbstractAggregationBuilder<*>>? = null,
        sort: Sort? = null
    ): ContentSearchPage {
        val pageRequest = PageRequest.of(page, pageSize)

        val isMatchPhraseQuery = isInQuotes(term)
        val baseQuery = baseQuery(term, isMatchPhraseQuery)

        return buildNativeSearchQuery {
            withQuery(
                baseQuery.applyFilters(preAggregationFilters)
                    .applyWeighting(TYPE, typeToWeight)
                    .applyWeighting(METATAGS, metatagToWeight)
            )
            withFilter(filters)
            withPageable(pageRequest)
            withHighlightBuilder(highlightBuilder(baseQuery, isMatchPhraseQuery))
            withTrackTotalHits(true)
            apply {
                aggregations?.let { withAggregations(it) }
                sort?.let { withSort(it) }
            }
        }
            .let { operations.search(it, ContentDao::class.java) }
            .let { SearchHitSupport.searchPageFor(it, pageRequest) }
            .let { mapper.toContentSearchPage(it, isMatchPhraseQuery) }

    }

    fun searchUrl(term: String): SearchHits<ContentDao> {
        return buildNativeSearchQuery {
            withQuery(
                searchUrlQuery(term)
                    .applyWeighting(TYPE, typeToWeight)
                    .applyWeighting(METATAGS, metatagToWeight)
            )
        }.let { operations.search(it, ContentDao::class.java) }
    }

    private fun buildNativeSearchQuery(builder: NativeSearchQueryBuilder.() -> NativeSearchQueryBuilder): NativeSearchQuery {
        return NativeSearchQueryBuilder().builder().build()
    }

    private fun baseQuery(term: String, isMatchPhraseQuery: Boolean): QueryBuilder {
        val (resolvedTerm, skjemanummer) = resolveTerm(term)
        return when {
            term.isBlank() -> MatchAllQueryBuilder()
            resolvedTerm.isBlank() && skjemanummer != null -> searchAllTextForPhraseQuery(skjemanummer)
            isMatchPhraseQuery -> searchAllTextForPhraseQuery(term)
            else -> searchAllTextQuery(resolvedTerm, skjemanummer)
        }
    }

    private fun resolveTerm(term: String): Pair<String, String?> {
        return term.replace(whitespace, "")
            .let { termsToOverride[it] ?: term }
            .let { extractSkjemanummerIfPresent(it) }
    }

    private fun extractSkjemanummerIfPresent(term: String): Pair<String, String?> {
        return skjemanummerRegex.find(term)?.let {
            term.replace(it.value, "") to "NAV ${it.groupValues[1]}-${it.groupValues[2]}.${it.groupValues[3]}"
        } ?: (term to null)
    }

    private fun isInQuotes(term: String): Boolean {
        return term.startsWith(QUOTE) && term.endsWith(QUOTE)
    }

    companion object {
        private const val QUOTE = '"'
        private const val SKJEMANUMMER_FORMAT = """(?:NAV|nav)?.?([0-9]{2}).?([0-9]{2}).?([0-9]{2})"""
        private val skjemanummerRegex = Regex(SKJEMANUMMER_FORMAT)
        val whitespace = "\\s+".toRegex()
    }
}