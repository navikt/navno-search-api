package no.nav.navnosearchapi.validation.enums

enum class ValidAudiences(override val descriptor: String) : DescriptorProvider {
    PRIVATPERSON("privatperson"),
    ARBEIDSGIVER("arbeidsgiver"),
    SAMARBEIDSPARTNER("samarbeidspartner"),
    ANDRE("andre"),
}