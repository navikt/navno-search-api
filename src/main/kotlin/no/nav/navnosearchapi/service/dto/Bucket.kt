package no.nav.navnosearchapi.service.dto

interface Bucket {
    val key: String
    val docCount: Long
    val checked: Boolean
}