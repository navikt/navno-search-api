package no.nav.navnosearchapi.search.compatibility.dto

interface Bucket {
    val key: String
    val docCount: Long
    val checked: Boolean
}