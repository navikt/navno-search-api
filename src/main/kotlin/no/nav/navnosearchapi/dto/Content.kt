package no.nav.navnosearchapi.dto

data class Content(
    val id: String,
    val href: String,
    val name: String,
    val ingress: String,
    val text: String,
    val maalgruppe: String,
    val language: String,
)