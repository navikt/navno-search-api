package no.nav.navnosearchapi.validation.enums

enum class ValidMetatags(override val descriptor: String) : DescriptorProvider {
    KONTOR("kontor"),
    SKJEMA("skjema"),
    NYHET("nyhet"),
    PRESSEMELDING("pressemelding"),
    NAV_OG_SAMFUNN("nav-og-samfunn"),
    ANALYSE("analyse"),
    STATISTIKK("statistikk"),
}