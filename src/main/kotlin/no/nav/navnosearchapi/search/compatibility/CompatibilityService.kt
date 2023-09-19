package no.nav.navnosearchapi.search.compatibility

import no.nav.navnosearchapi.common.enums.ValidAudiences
import no.nav.navnosearchapi.common.enums.ValidFylker
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.enumDescriptors
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import no.nav.navnosearchapi.search.service.search.Filters
import org.opensearch.index.query.QueryBuilder
import org.springframework.stereotype.Component

@Component
class CompatibilityService(val searchResultMapper: SearchResultMapper) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toFilters(f: String?, uf: List<String>?, daterange: Int?): List<QueryBuilder> {
        val filters = when (f) {
            "0" -> {
                if (uf.isNullOrEmpty()) {
                    Filters(metatags = listOf(ValidMetatags.INNHOLD.descriptor))
                } else {
                    val metatags = mutableListOf<String>()
                    if (uf.contains("0")) metatags.add(ValidMetatags.INFORMASJON.descriptor)
                    if (uf.contains("1")) metatags.add(ValidMetatags.KONTOR.descriptor)
                    if (uf.contains("2")) metatags.add(ValidMetatags.SKJEMA.descriptor)
                    Filters(metatags = metatags)
                }
            }

            "en" -> Filters(language = listOf(ENGLISH))
            "1" -> {
                if (uf.isNullOrEmpty()) {
                    Filters(metatags = listOf(ValidMetatags.NYHET.descriptor))
                } else {
                    val metatags = mutableListOf<String>()
                    val audience = mutableListOf<String>()
                    if (uf.contains("1")) audience.add(ValidAudiences.PRIVATPERSON.descriptor)
                    if (uf.contains("2")) audience.add(ValidAudiences.ARBEIDSGIVER.descriptor)
                    if (uf.contains("4")) metatags.add(ValidMetatags.STATISTIKK.descriptor)
                    if (uf.contains("0")) metatags.add(ValidMetatags.PRESSE.descriptor)
                    if (uf.contains("pm")) metatags.add(ValidMetatags.PRESSEMELDING.descriptor)
                    if (uf.contains("5")) metatags.add(ValidMetatags.NAV_OG_SAMFUNN.descriptor)
                    Filters(metatags = metatags, audience = audience)
                }
            }

            "5" -> Filters(metatags = listOf(ValidMetatags.ANALYSE.descriptor))
            "3" -> Filters(metatags = listOf(ValidMetatags.STATISTIKK.descriptor))
            "4" -> {
                if (uf.isNullOrEmpty()) {
                    Filters(
                        fylke = enumDescriptors<ValidFylker>() // Alle fylker
                    )
                } else {
                    val fylke = mutableListOf<String>()
                    if (uf.contains("0")) fylke.add(ValidFylker.AGDER.descriptor)
                    if (uf.contains("1")) fylke.add(ValidFylker.INNLANDET.descriptor)
                    if (uf.contains("2")) fylke.add(ValidFylker.MORE_OG_ROMSDAL.descriptor)
                    if (uf.contains("3")) fylke.add(ValidFylker.NORDLAND.descriptor)
                    if (uf.contains("4")) fylke.add(ValidFylker.OSLO.descriptor)
                    if (uf.contains("5")) fylke.add(ValidFylker.ROGALAND.descriptor)
                    if (uf.contains("6")) fylke.add(ValidFylker.TROMS_OG_FINNMARK.descriptor)
                    if (uf.contains("7")) fylke.add(ValidFylker.TRONDELAG.descriptor)
                    if (uf.contains("8")) fylke.add(ValidFylker.VESTFOLD_OG_TELEMARK.descriptor)
                    if (uf.contains("9")) fylke.add(ValidFylker.VESTLAND.descriptor)
                    if (uf.contains("10")) fylke.add(ValidFylker.VEST_VIKEN.descriptor)
                    if (uf.contains("11")) fylke.add(ValidFylker.OST_VIKEN.descriptor)

                    Filters(fylke = fylke)
                }
            }

            "2" -> Filters(isFile = listOf(true.toString()))
            else -> Filters()
        }
        return filters.toQueryList()
    }
}