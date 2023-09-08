package no.nav.navnosearchapi.validation.enums

enum class ValidLanguages(override val descriptor: String) : DescriptorProvider {
    NB("nb"),
    NN("nn"),
    EN("en"),
    SE("se"),
    PL("pl"),
    UK("uk"),
    RU("ru"),
    OTHER("other"),
}