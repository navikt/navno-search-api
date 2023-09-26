package no.nav.navnosearchapi.search.compatibility

import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.search.compatibility.dto.SearchResult
import no.nav.navnosearchapi.search.compatibility.filters.fasettFilters
import no.nav.navnosearchapi.search.compatibility.filters.fylkeFilters
import no.nav.navnosearchapi.search.compatibility.filters.innholdFilters
import no.nav.navnosearchapi.search.compatibility.filters.nyheterFilters
import no.nav.navnosearchapi.search.compatibility.filters.tidsperiodeFilters
import no.nav.navnosearchapi.search.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_12_MONTHS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_7_DAYS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_AGDER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INNLANDET
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_MORE_OG_ROMSDAL
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_NORDLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OSLO
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_OST_VIKEN
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_ROGALAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TROMS_OG_FINNMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_TRONDELAG
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTFOLD_OG_TELEMARK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VESTLAND
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_VEST_VIKEN
import no.nav.navnosearchapi.search.dto.ContentSearchPage
import org.opensearch.index.query.QueryBuilder
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.springframework.stereotype.Component

@Component
class CompatibilityService(val searchResultMapper: SearchResultMapper) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toFilters(f: String?, uf: List<String>?, daterange: Int?): List<QueryBuilder> {
        return when (f) {
            FASETT_INNHOLD -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD]?.filters
                } else {
                    uf.flatMap { innholdFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_ENGLISH -> fasettFilters[ENGLISH]?.filters
            FASETT_NYHETER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_NYHETER]?.filters
                } else {
                    uf.flatMap { nyheterFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_ANALYSER_OG_FORSKNING -> fasettFilters[FASETT_ANALYSER_OG_FORSKNING]?.filters
            FASETT_STATISTIKK -> fasettFilters[FASETT_STATISTIKK]?.filters
            FASETT_INNHOLD_FRA_FYLKER -> {
                if (uf.isNullOrEmpty()) {
                    fasettFilters[FASETT_INNHOLD_FRA_FYLKER]?.filters
                } else {
                    uf.flatMap { fylkeFilters[it]?.filters ?: emptyList() }
                }
            }

            FASETT_FILER -> fasettFilters[FASETT_FILER]?.filters
            else -> emptyList()
        } ?: emptyList()
    }

    fun aggregations(filters: List<QueryBuilder>): List<FilterAggregationBuilder> {
        return listOfNotNull(
            fasettFilters[FASETT_INNHOLD]?.toAggregation(),
            fasettFilters[FASETT_ENGLISH]?.toAggregation(),
            fasettFilters[FASETT_NYHETER]?.toAggregation(),
            fasettFilters[FASETT_ANALYSER_OG_FORSKNING]?.toAggregation(),
            fasettFilters[FASETT_STATISTIKK]?.toAggregation(),
            fasettFilters[FASETT_INNHOLD_FRA_FYLKER]?.toAggregation(),
            fasettFilters[FASETT_FILER]?.toAggregation(),
            innholdFilters[UNDERFASETT_INFORMASJON]?.toAggregation(),
            innholdFilters[UNDERFASETT_KONTOR]?.toAggregation(),
            innholdFilters[UNDERFASETT_SOKNAD_OG_SKJEMA]?.toAggregation(),
            nyheterFilters[UNDERFASETT_PRIVATPERSON]?.toAggregation(),
            nyheterFilters[UNDERFASETT_ARBEIDSGIVER]?.toAggregation(),
            nyheterFilters[UNDERFASETT_STATISTIKK]?.toAggregation(),
            nyheterFilters[UNDERFASETT_PRESSE]?.toAggregation(),
            nyheterFilters[UNDERFASETT_PRESSEMELDINGER]?.toAggregation(),
            nyheterFilters[UNDERFASETT_NAV_OG_SAMFUNN]?.toAggregation(),
            fylkeFilters[UNDERFASETT_AGDER]?.toAggregation(),
            fylkeFilters[UNDERFASETT_INNLANDET]?.toAggregation(),
            fylkeFilters[UNDERFASETT_MORE_OG_ROMSDAL]?.toAggregation(),
            fylkeFilters[UNDERFASETT_NORDLAND]?.toAggregation(),
            fylkeFilters[UNDERFASETT_OSLO]?.toAggregation(),
            fylkeFilters[UNDERFASETT_ROGALAND]?.toAggregation(),
            fylkeFilters[UNDERFASETT_TROMS_OG_FINNMARK]?.toAggregation(),
            fylkeFilters[UNDERFASETT_TRONDELAG]?.toAggregation(),
            fylkeFilters[UNDERFASETT_VESTFOLD_OG_TELEMARK]?.toAggregation(),
            fylkeFilters[UNDERFASETT_VESTLAND]?.toAggregation(),
            fylkeFilters[UNDERFASETT_VEST_VIKEN]?.toAggregation(),
            fylkeFilters[UNDERFASETT_OST_VIKEN]?.toAggregation(),
            tidsperiodeFilters[TIDSPERIODE_OLDER_THAN_12_MONTHS]?.toAggregation(filters),
            tidsperiodeFilters[TIDSPERIODE_LAST_12_MONTHS]?.toAggregation(filters),
            tidsperiodeFilters[TIDSPERIODE_LAST_30_DAYS]?.toAggregation(filters),
            tidsperiodeFilters[TIDSPERIODE_LAST_7_DAYS]?.toAggregation(filters),
        )
    }
}