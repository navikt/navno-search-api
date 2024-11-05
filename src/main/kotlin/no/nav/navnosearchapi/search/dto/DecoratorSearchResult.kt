package no.nav.navnosearchapi.search.dto

data class DecoratorSearchResult(
    val preferredLanguage: String?,
    val word: String,
    val total: Long,
    val hits: List<DecoratorSearchHit>
)