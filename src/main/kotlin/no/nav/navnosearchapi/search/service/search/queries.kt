package no.nav.navnosearchapi.search.service.search

import no.nav.navnosearchapi.common.utils.AUTOCOMPLETE_SEARCH_AS_YOU_TYPE
import no.nav.navnosearchapi.common.utils.INGRESS_WILDCARD
import no.nav.navnosearchapi.common.utils.TEXT_WILDCARD
import no.nav.navnosearchapi.common.utils.TITLE_WILDCARD
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder
import org.opensearch.index.query.MultiMatchQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.RangeQueryBuilder
import org.opensearch.index.query.TermsQueryBuilder
import java.time.ZonedDateTime

private const val TITLE_WEIGHT = 3.0f
private const val INGRESS_WEIGHT = 2.0f
private const val TEXT_WEIGHT = 1.0f

private val fieldsToWeightMap = mapOf(
    TITLE_WILDCARD to TITLE_WEIGHT,
    INGRESS_WILDCARD to INGRESS_WEIGHT,
    TEXT_WILDCARD to TEXT_WEIGHT
)

fun searchAllTextQuery(term: String): MultiMatchQueryBuilder {
    return MultiMatchQueryBuilder(term).fields(fieldsToWeightMap)
}

fun searchAllTextForPhraseQuery(term: String): MultiMatchQueryBuilder {
    return MultiMatchQueryBuilder(term).fields(fieldsToWeightMap).type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun searchAsYouTypeQuery(term: String): MatchPhrasePrefixQueryBuilder {
    return MatchPhrasePrefixQueryBuilder(AUTOCOMPLETE_SEARCH_AS_YOU_TYPE, term)
}

fun filteredQuery(baseQuery: QueryBuilder, filters: List<QueryBuilder>): BoolQueryBuilder {
    val query = BoolQueryBuilder().must(baseQuery)
    filters.forEach { query.filter(it) }
    return query
}

fun termsQuery(field: String, values: List<String>): TermsQueryBuilder {
    return TermsQueryBuilder(field, values)
}

fun rangeQuery(field: String, gte: ZonedDateTime?, lte: ZonedDateTime?): RangeQueryBuilder {
    return RangeQueryBuilder(field).from(gte).to(lte)
}