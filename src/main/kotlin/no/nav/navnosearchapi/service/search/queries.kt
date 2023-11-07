package no.nav.navnosearchapi.service.search

import no.nav.navnosearchadminapi.common.constants.INGRESS_WILDCARD
import no.nav.navnosearchadminapi.common.constants.KEYWORDS
import no.nav.navnosearchadminapi.common.constants.TEXT_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TITLE_WILDCARD
import org.opensearch.common.unit.Fuzziness
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.MultiMatchQueryBuilder
import org.opensearch.index.query.RangeQueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import java.time.ZonedDateTime

private const val TITLE_WEIGHT = 7.0f
private const val INGRESS_WEIGHT = 5.0f
private const val TEXT_WEIGHT = 1.0f
private const val KEYWORDS_WEIGHT = 7.0f

private val fieldsToWeightMap = mapOf(
    TITLE_WILDCARD to TITLE_WEIGHT,
    INGRESS_WILDCARD to INGRESS_WEIGHT,
    TEXT_WILDCARD to TEXT_WEIGHT,
    KEYWORDS to KEYWORDS_WEIGHT,
)

fun searchAllTextQuery(term: String): MultiMatchQueryBuilder {
    return MultiMatchQueryBuilder(term)
        .fields(fieldsToWeightMap)
        .fuzziness(Fuzziness.AUTO)
        .type(MultiMatchQueryBuilder.Type.MOST_FIELDS)
}

fun searchAllTextForPhraseQuery(term: String): MultiMatchQueryBuilder {
    return MultiMatchQueryBuilder(term).fields(fieldsToWeightMap).type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun termQuery(field: String, value: String): TermQueryBuilder {
    return TermQueryBuilder(field, value)
}

fun existsQuery(field: String): ExistsQueryBuilder {
    return ExistsQueryBuilder(field)
}

fun rangeQuery(field: String, gte: ZonedDateTime?, lte: ZonedDateTime?): RangeQueryBuilder {
    return RangeQueryBuilder(field).from(gte).to(lte)
}