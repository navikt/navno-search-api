package no.nav.navnosearchapi.service.search.queries

import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

fun termQuery(field: String, value: String): TermQueryBuilder {
    return TermQueryBuilder(field, value)
}

fun existsQuery(field: String): ExistsQueryBuilder {
    return ExistsQueryBuilder(field)
}