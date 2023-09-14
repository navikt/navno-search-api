package no.nav.navnosearchapi.common.enums

enum class ValidMetatags(override val descriptor: String) : DescriptorProvider {
    INNHOLD("innhold"),
    INFORMASJON("informasjon"),
    KONTOR("kontor"),
    SKJEMA("skjema"),
    NYHET("nyhet"),
    PRESSE("presse"),
    PRESSEMELDING("pressemelding"),
    NAV_OG_SAMFUNN("nav-og-samfunn"),
    ANALYSE("analyse"),
    STATISTIKK("statistikk"),
}