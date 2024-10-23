package no.nav.navnosearchapi.client.utils

import no.nav.navnosearchapi.client.SearchClient
import org.opensearch.common.lucene.search.function.FunctionScoreQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.DisMaxQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders

fun QueryBuilder.applyFilters(filterQuery: BoolQueryBuilder?): QueryBuilder {
    return filterQuery?.let { BoolQueryBuilder().must(this).filter(filterQuery) } ?: this
}

fun QueryBuilder.applyWeighting(field: String, fieldToWeightMap: Map<String, Float>): FunctionScoreQueryBuilder {
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

fun DisMaxQueryBuilder.addIfTermHasMinLength(
    term: String,
    minLength: Int,
    query: QueryBuilder
): DisMaxQueryBuilder {
    if (term.trim().length >= minLength) {
        this.add(query)
    }
    return this
}

fun DisMaxQueryBuilder.addIfMultipleWordsInTerm(term: String, query: QueryBuilder): DisMaxQueryBuilder {
    if (term.split(SearchClient.whitespace).size > 1) {
        this.add(query)
    }
    return this
}