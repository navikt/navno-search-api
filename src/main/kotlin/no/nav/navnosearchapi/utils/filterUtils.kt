import org.opensearch.index.query.BoolQueryBuilder

fun joinClausesToSingleQuery(
    shouldClauses: List<BoolQueryBuilder> = emptyList(),
    mustClauses: List<BoolQueryBuilder> = emptyList()
): BoolQueryBuilder {
    val joinedQuery = BoolQueryBuilder()
    shouldClauses.forEach { joinedQuery.should(it) }
    mustClauses.forEach { joinedQuery.must(it) }
    return joinedQuery
}