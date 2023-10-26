package no.nav.navnosearchapi.search.search.filter

import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.QueryBuilder

data class Filters(
    val fasetter: List<QueryBuilder> = emptyList(),
    val tidsperioder: List<QueryBuilder> = emptyList(),
) {
    fun toQuery(): QueryBuilder {
        val mainQuery = BoolQueryBuilder()
        val fasettQuery = BoolQueryBuilder()

        fasetter.forEach { fasettQuery.should(it) }

        mainQuery.must(fasettQuery)
        tidsperioder.forEach { mainQuery.must(it) }

        return mainQuery
    }
}