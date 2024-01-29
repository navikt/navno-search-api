package no.nav.navnosearchapi.service.compatibility

data class Params(
    val ord: String, // Term
    val c: Int = 1, // Number of results to retrieve (20 * c)
    val start: Int = 0, // Skips first (20 * start) results
    val f: String = "0", // Facet key
    val uf: List<String> = emptyList(), // Under-facet keys
    val s: Int = 0, // Sort
    val daterange: Int = -1, // Daterange key
    val audience: String? = null,
    val preferredLanguage: String? = null,
)