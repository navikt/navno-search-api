package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.utils.INGRESS
import no.nav.navnosearchapi.utils.TEXT
import no.nav.navnosearchapi.utils.TITLE
import no.nav.navnosearchapi.utils.enumDescriptors
import no.nav.navnosearchapi.validation.enums.ValidLanguages
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

private val fieldsToWeightMap = mapOf(TITLE to TITLE_WEIGHT, INGRESS to INGRESS_WEIGHT, TEXT to TEXT_WEIGHT)

fun searchAllTextQuery(term: String): MultiMatchQueryBuilder = MultiMatchQueryBuilder(term).fields(fields())

fun searchAllTextForPhraseQuery(term: String): MultiMatchQueryBuilder =
    MultiMatchQueryBuilder(term).fields(fields()).type(MultiMatchQueryBuilder.Type.PHRASE)

fun searchAsYouTypeQuery(term: String) = MatchPhrasePrefixQueryBuilder("title.searchAsYouType", term)

fun filteredQuery(baseQuery: QueryBuilder, filters: List<QueryBuilder>): BoolQueryBuilder {
    val query = BoolQueryBuilder().must(baseQuery)
    filters.forEach { query.filter(it) }
    return query
}

fun termsQuery(field: String, values: List<String>) = TermsQueryBuilder(field, values)

fun rangeQuery(field: String, gte: ZonedDateTime?, lte: ZonedDateTime?) = RangeQueryBuilder(field).from(gte).to(lte)

private fun fields(): Map<String, Float> {
    return fieldsToWeightMap.flatMap { (field, weight) ->
        enumDescriptors<ValidLanguages>().map { language ->
            "$field.$language" to weight
        }
    }.toMap()
}