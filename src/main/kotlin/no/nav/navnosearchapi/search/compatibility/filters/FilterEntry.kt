package no.nav.navnosearchapi.search.compatibility.filters

import org.opensearch.index.query.QueryBuilder

data class FilterEntry(val name: String, val filters: List<QueryBuilder>)