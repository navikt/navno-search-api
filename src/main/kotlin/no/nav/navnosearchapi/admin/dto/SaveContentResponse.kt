package no.nav.navnosearchapi.admin.dto

data class SaveContentResponse(
    val numberOfIndexedDocuments: Int,
    val numberOfFailedDocuments: Int,
    val validationErrors: Map<String, List<String>>
)