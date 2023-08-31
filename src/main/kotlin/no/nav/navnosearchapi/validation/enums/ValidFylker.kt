package no.nav.navnosearchapi.validation.enums

enum class ValidFylker(override val descriptor: String) : DescriptorProvider {
    AGDER("agder"),
    INNLANDET("innlandet"),
    MORE_OG_ROMSDAL("more-og-romsdal"),
    NORDLAND("nordland"),
    OSLO("oslo"),
    ROGALAND("rogaland"),
    TROMS_OG_FINNMARK("troms-og-finnmark"),
    TRONDELAG("trondelag"),
    VESTFOLD_OG_TELEMARK("vestfold-og-telemark"),
    VESTLAND("vestland"),
    VEST_VIKEN("vest-viken"),
    OST_VIKEN("ost-viken"),
}