package no.nav.navnosearchapi.service.compatibility.dto

interface Bucket {
    val key: String
    val docCount: Long
    val checked: Boolean
}