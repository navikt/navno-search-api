package no.nav.navnosearchapi.validation

enum class ValidLanguages(private val descriptor: String) {
    NO("no"), EN("en"), OTHER("other");

    override fun toString(): String {
        return descriptor
    }
}