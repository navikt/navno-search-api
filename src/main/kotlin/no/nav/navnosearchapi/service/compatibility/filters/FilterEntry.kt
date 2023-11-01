package no.nav.navnosearchapi.service.compatibility.filters

import org.opensearch.index.query.BoolQueryBuilder

data class FilterEntry(val name: String, val filterQuery: BoolQueryBuilder)