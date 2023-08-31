package no.nav.navnosearchapi.dto

data class ContentMetadata(
    val audience: List<String>,
    val language: String,
    val isFile: Boolean? = null,
    val fylke: String? = null,
    val metatags: List<String>? = null,
)