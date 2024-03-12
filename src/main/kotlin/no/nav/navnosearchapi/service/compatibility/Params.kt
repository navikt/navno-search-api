package no.nav.navnosearchapi.service.compatibility

import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys

data class Params(
    val ord: String, // Term
    val page: Int = 0,
    val f: String = FacetKeys.PRIVATPERSON, // Facet key
    val uf: List<String> = emptyList(), // Under-facet keys
    val s: Int = 0, // Sort
    val preferredLanguage: String? = null,
)