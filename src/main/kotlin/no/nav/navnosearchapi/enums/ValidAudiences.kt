package no.nav.navnosearchapi.enums

// todo: skrive seg bort fra disse?
enum class ValidAudiences(override val descriptor: String) : DescriptorProvider {
    PRIVATPERSON("privatperson"),
    ARBEIDSGIVER("arbeidsgiver"),
    SAMARBEIDSPARTNER("samarbeidspartner"),
    ANDRE("andre"),
}