package no.nav.navnosearchapi.search.compatibility

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.enumDescriptors
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.service.search.Filter
import org.opensearch.index.query.QueryBuilder
import org.springframework.stereotype.Component

@Component
class CompatibilityService(val searchResultMapper: SearchResultMapper) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toFilters(f: String?, uf: List<String>?, daterange: Int?): List<QueryBuilder> {
        val filters = when (f) {
            FASETT_INNHOLD -> {
                if (uf.isNullOrEmpty()) {
                    Filter(
                        metatags = listOf(
                            ValidMetatags.INFORMASJON.descriptor,
                            ValidMetatags.KONTOR.descriptor,
                            ValidMetatags.SKJEMA.descriptor
                        )
                    )
                } else {
                    val metatags = mutableListOf<String>()
                    if (uf.contains(UNDERFASETT_INFORMASJON)) metatags.add(ValidMetatags.INFORMASJON.descriptor)
                    if (uf.contains(UNDERFASETT_KONTOR)) metatags.add(ValidMetatags.KONTOR.descriptor)
                    if (uf.contains(UNDERFASETT_SOKNAD_OG_SKJEMA)) metatags.add(ValidMetatags.SKJEMA.descriptor)
                    Filter(metatags = metatags)
                }
            }

            FASETT_ENGLISH -> Filter(language = listOf(ENGLISH))
            FASETT_NYHETER -> {
                if (uf.isNullOrEmpty()) {
                    Filter(metatags = listOf(ValidMetatags.NYHET.descriptor))
                } else {
                    val metatags = mutableListOf<String>()
                    val audience = mutableListOf<String>()
                    if (uf.contains(UNDERFASETT_PRIVATPERSON)) audience.add(ValidAudiences.PRIVATPERSON.descriptor)
                    if (uf.contains(UNDERFASETT_ARBEIDSGIVER)) audience.add(ValidAudiences.ARBEIDSGIVER.descriptor)
                    if (uf.contains(UNDERFASETT_STATISTIKK)) metatags.add(ValidMetatags.STATISTIKK.descriptor)
                    if (uf.contains(UNDERFASETT_PRESSE)) metatags.add(ValidMetatags.PRESSE.descriptor)
                    if (uf.contains(UNDERFASETT_PRESSEMELDINGER)) metatags.add(ValidMetatags.PRESSEMELDING.descriptor)
                    if (uf.contains(UNDERFASETT_NAV_OG_SAMFUNN)) metatags.add(ValidMetatags.NAV_OG_SAMFUNN.descriptor)
                    Filter(metatags = metatags, audience = audience)
                }
            }

            FASETT_ANALYSER_OG_FORSKNING -> Filter(metatags = listOf(ValidMetatags.ANALYSE.descriptor))
            FASETT_STATISTIKK -> Filter(metatags = listOf(ValidMetatags.STATISTIKK.descriptor))
            FASETT_INNHOLD_FRA_FYLKER -> {
                if (uf.isNullOrEmpty()) {
                    Filter(
                        fylke = enumDescriptors<ValidFylker>() // Alle fylker
                    )
                } else {
                    val fylke = mutableListOf<String>()
                    if (uf.contains(UNDERFASETT_AGDER)) fylke.add(ValidFylker.AGDER.descriptor)
                    if (uf.contains(UNDERFASETT_INNLANDET)) fylke.add(ValidFylker.INNLANDET.descriptor)
                    if (uf.contains(UNDERFASETT_MORE_OG_ROMSDAL)) fylke.add(ValidFylker.MORE_OG_ROMSDAL.descriptor)
                    if (uf.contains(UNDERFASETT_NORDLAND)) fylke.add(ValidFylker.NORDLAND.descriptor)
                    if (uf.contains(UNDERFASETT_OSLO)) fylke.add(ValidFylker.OSLO.descriptor)
                    if (uf.contains(UNDERFASETT_ROGALAND)) fylke.add(ValidFylker.ROGALAND.descriptor)
                    if (uf.contains(UNDERFASETT_TROMS_OG_FINNMARK)) fylke.add(ValidFylker.TROMS_OG_FINNMARK.descriptor)
                    if (uf.contains(UNDERFASETT_TRONDELAG)) fylke.add(ValidFylker.TRONDELAG.descriptor)
                    if (uf.contains(UNDERFASETT_VESTFOLD_OG_TELEMARK)) fylke.add(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor)
                    if (uf.contains(UNDERFASETT_VESTLAND)) fylke.add(ValidFylker.VESTLAND.descriptor)
                    if (uf.contains(UNDERFASETT_VEST_VIKEN)) fylke.add(ValidFylker.VEST_VIKEN.descriptor)
                    if (uf.contains(UNDERFASETT_OST_VIKEN)) fylke.add(ValidFylker.OST_VIKEN.descriptor)

                    Filter(fylke = fylke)
                }
            }

            FASETT_FILER -> Filter(isFile = listOf(true.toString()))
            else -> Filter()
        }
        return listOf(filters.toQuery())
    }
}