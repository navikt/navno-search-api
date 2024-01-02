package no.nav.navnosearchapi.service.search.queries

import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.RangeQueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import java.time.ZonedDateTime

fun termQuery(field: String, value: String): TermQueryBuilder {
    return TermQueryBuilder(field, value)
}

fun existsQuery(field: String): ExistsQueryBuilder {
    return ExistsQueryBuilder(field)
}

fun rangeQuery(field: String, gte: ZonedDateTime? = null, lte: ZonedDateTime? = null): RangeQueryBuilder {
    return RangeQueryBuilder(field).from(gte).to(lte)
}