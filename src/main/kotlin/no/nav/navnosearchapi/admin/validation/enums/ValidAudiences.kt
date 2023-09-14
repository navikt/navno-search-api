package no.nav.navnosearchapi.admin.validation.enums

enum class ValidAudiences(override val descriptor: String) : DescriptorProvider {
    PRIVATPERSON("privatperson"),
    ARBEIDSGIVER("arbeidsgiver"),
    SAMARBEIDSPARTNER("samarbeidspartner"),
    ANDRE("andre"),
}