package no.nav.navnosearchapi.common.enums

enum class ValidMetatags(override val descriptor: String) : DescriptorProvider {
    INNHOLD("innhold"), //todo: fjerne
    INFORMASJON("informasjon"), //todo: sette som default dersom ingen andre metatags, ingen fylker, ikke fil
    KONTOR("kontor"),
    SKJEMA("skjema"),
    NYHET("nyhet"),
    PRESSE("presse"),
    PRESSEMELDING("pressemelding"),
    NAV_OG_SAMFUNN("nav-og-samfunn"),
    ANALYSE("analyse"),
    STATISTIKK("statistikk"),
}