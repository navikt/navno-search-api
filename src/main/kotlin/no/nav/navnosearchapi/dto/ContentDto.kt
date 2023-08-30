package no.nav.navnosearchapi.dto

import no.nav.navnosearchapi.validation.ValidLanguages

data class ContentDto(
    val id: String,
    val href: String,
    val title: String,
    val ingress: String,
    val text: String,
    val audience: List<String>,
    val language: String, /** Gyldige verdier: [ValidLanguages] */
    val isFile: Boolean? = null,
    val fylke: String? = null,
    val metatags: List<String>? = null,
)