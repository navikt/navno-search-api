package no.nav.navnosearchapi.utils

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates

const val INDEX_PREFIX = "search-content_"

fun indexName(appName: String): String {
    return INDEX_PREFIX + appName
}

fun indexCoordinates(appName: String): IndexCoordinates {
    return IndexCoordinates.of(indexName(appName))
}

fun defaultIndexCoordinates(): IndexCoordinates {
    return IndexCoordinates.of("search-content")
}