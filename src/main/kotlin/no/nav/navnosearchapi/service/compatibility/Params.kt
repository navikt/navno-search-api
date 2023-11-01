package no.nav.navnosearchapi.service.compatibility

data class Params(
    val ord: String, // Term
    val c: Int, // Number of results to retrieve (20 * c)
    val start: Int, // Skips first (20 * start) results
    val f: String, // Facet key
    val uf: List<String>, // Under-facet keys
    val s: Int, // Sort
    val daterange: Int, // Daterange key
)