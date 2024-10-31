package no.nav.navnosearchapi.client

import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.model.Content
import no.nav.navnosearchapi.client.config.metatagToWeight
import no.nav.navnosearchapi.client.config.termsToOverride
import no.nav.navnosearchapi.client.config.typeToWeight
import no.nav.navnosearchapi.client.queries.highlightBuilder
import no.nav.navnosearchapi.client.queries.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.client.queries.searchAllTextQuery
import no.nav.navnosearchapi.client.queries.searchUrlQuery
import org.opensearch.common.lucene.search.function.FunctionScoreQuery
import org.opensearch.data.client.orhlc.NativeSearchQuery
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchAllQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders
import org.opensearch.search.aggregations.AbstractAggregationBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.stereotype.Component

@Component
class SearchClient(
    val operations: ElasticsearchOperations,
) {
    fun search(
        term: String,
        isMatchPhraseQuery: Boolean,
        filters: BoolQueryBuilder,
        preAggregationFilters: BoolQueryBuilder? = null,
        aggregations: List<AbstractAggregationBuilder<*>>? = null,
        sort: Sort? = null,
        pageRequest: PageRequest,
    ): SearchPage<Content> {
        return baseQuery(term, isMatchPhraseQuery).let { baseQuery ->
            buildNativeSearchQuery {
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
                .let { operations.search(it, Content::class.java) }
                .let { SearchHitSupport.searchPageFor(it, pageRequest) }
        }
    }

    fun searchUrl(term: String): SearchHits<Content> {
        return buildNativeSearchQuery {
            withQuery(
                searchUrlQuery(term)
                    .applyWeighting(TYPE, typeToWeight)
                    .applyWeighting(METATAGS, metatagToWeight)
            )
        }.let { operations.search(it, Content::class.java) }
    }

    private fun buildNativeSearchQuery(builder: NativeSearchQueryBuilder.() -> NativeSearchQueryBuilder): NativeSearchQuery {
        return NativeSearchQueryBuilder().builder().build()
    }

    private fun baseQuery(term: String, isMatchPhraseQuery: Boolean): QueryBuilder {
        return resolveTerm(term).let { (resolvedTerm, skjemanummer) ->
            when {
                term.isBlank() -> MatchAllQueryBuilder()
                resolvedTerm.isBlank() && skjemanummer != null -> searchAllTextForPhraseQuery(skjemanummer)
                isMatchPhraseQuery -> searchAllTextForPhraseQuery(term)
                else -> searchAllTextQuery(resolvedTerm, skjemanummer)
            }
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

    private fun QueryBuilder.applyFilters(filterQuery: BoolQueryBuilder?): QueryBuilder {
        return filterQuery?.let { BoolQueryBuilder().must(this).filter(filterQuery) } ?: this
    }

    private fun QueryBuilder.applyWeighting(
        field: String,
        fieldToWeightMap: Map<String, Float>
    ): FunctionScoreQueryBuilder {
        return FunctionScoreQueryBuilder(
            this,
            fieldToWeightMap.map {
                FunctionScoreQueryBuilder.FilterFunctionBuilder(
                    TermQueryBuilder(field, it.key),
                    ScoreFunctionBuilders.weightFactorFunction(it.value)
                )
            }.toTypedArray()
        ).scoreMode(FunctionScoreQuery.ScoreMode.MAX)
    }

    companion object {
        private const val SKJEMANUMMER_FORMAT = """(?:NAV|nav)?.?([0-9]{2}).?([0-9]{2}).?([0-9]{2})"""
        private val skjemanummerRegex = Regex(SKJEMANUMMER_FORMAT)
        val whitespace = "\\s+".toRegex()
    }
}