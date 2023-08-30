package no.nav.navnosearchapi.validation.enums

enum class ValidLanguages(override val descriptor: String) : DescriptorProvider {
    NO("no"), EN("en"), OTHER("other");
}