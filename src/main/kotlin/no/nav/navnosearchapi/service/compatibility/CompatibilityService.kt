package no.nav.navnosearchapi.service.compatibility

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE_REFS
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchapi.service.compatibility.dto.DecoratorSearchResult
import no.nav.navnosearchapi.service.compatibility.dto.SearchResult
import no.nav.navnosearchapi.service.compatibility.filters.FilterEntry
import no.nav.navnosearchapi.service.compatibility.filters.analyseFilters
import no.nav.navnosearchapi.service.compatibility.filters.arbeidsgiverFilters
import no.nav.navnosearchapi.service.compatibility.filters.fasettFilters
import no.nav.navnosearchapi.service.compatibility.filters.fylkeFilters
import no.nav.navnosearchapi.service.compatibility.filters.privatpersonFilters
import no.nav.navnosearchapi.service.compatibility.filters.samarbeidspartnerFilters
import no.nav.navnosearchapi.service.compatibility.filters.statistikkFilters
import no.nav.navnosearchapi.service.compatibility.mapper.DecoratorSearchResultMapper
import no.nav.navnosearchapi.service.compatibility.mapper.SearchResultMapper
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.service.search.dto.ContentSearchPage
import no.nav.navnosearchapi.utils.joinClausesToSingleQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder
import org.springframework.stereotype.Component

@Component
class CompatibilityService(
    val searchResultMapper: SearchResultMapper,
    val decoratorSearchResultMapper: DecoratorSearchResultMapper
) {
    fun toSearchResult(params: Params, result: ContentSearchPage): SearchResult {
        return searchResultMapper.toSearchResult(params, result)
    }

    fun toDecoratorSearchResult(params: Params, result: ContentSearchPage): DecoratorSearchResult {
        return decoratorSearchResultMapper.toSearchResult(params, result)
    }

    fun preAggregationFilters(preferredLanguage: String?): BoolQueryBuilder {
        return BoolQueryBuilder().apply {
            if (preferredLanguage != null) {
                this.must(activePreferredLanguageFilterQuery(preferredLanguage))
            }
        }
    }

    fun postAggregationFilters(f: String, uf: List<String>): BoolQueryBuilder {
        return BoolQueryBuilder().must(activeFasettFilterQuery(f, uf))
    }

    fun decoratorSearchFilters(facet: String, preferredLanguage: String?): BoolQueryBuilder {
        return BoolQueryBuilder().must(activeFasettFilterQuery(facet, emptyList())).apply {
            if (preferredLanguage != null) {
                this.must(activePreferredLanguageFilterQuery(preferredLanguage))
            }
        }
    }

    fun aggregations(f: String, uf: List<String>): List<FilterAggregationBuilder> {
        return (fasettFilters.values +
                privatpersonFilters.values +
                arbeidsgiverFilters.values +
                samarbeidspartnerFilters.values +
                statistikkFilters.values +
                analyseFilters.values +
                fylkeFilters.values).map { AggregationBuilders.filter(it.aggregationName, it.filterQuery) }
    }

    private fun activeFasettFilterQuery(f: String, uf: List<String>): BoolQueryBuilder {
        fun filterWithUnderfacets(facetKey: String, ufFilters: Map<String, FilterEntry>) = if (uf.isEmpty()) {
            fasettFilters[facetKey]!!.filterQuery
        } else {
            joinClausesToSingleQuery(shouldClauses = uf.map { ufFilters[it]!!.filterQuery })
        }

        fun filterWithoutUnderfacets(facetKey: String) = fasettFilters[facetKey]!!.filterQuery

        return when (f) {
            FacetKeys.PRIVATPERSON -> filterWithUnderfacets(FacetKeys.PRIVATPERSON, privatpersonFilters)
            FacetKeys.ARBEIDSGIVER -> filterWithUnderfacets(FacetKeys.ARBEIDSGIVER, arbeidsgiverFilters)
            FacetKeys.SAMARBEIDSPARTNER -> filterWithUnderfacets(FacetKeys.SAMARBEIDSPARTNER, samarbeidspartnerFilters)
            FacetKeys.PRESSE -> filterWithoutUnderfacets(FacetKeys.PRESSE)
            FacetKeys.ANALYSER_OG_FORSKNING -> filterWithUnderfacets(FacetKeys.ANALYSER_OG_FORSKNING, analyseFilters)
            FacetKeys.STATISTIKK -> filterWithUnderfacets(FacetKeys.STATISTIKK, statistikkFilters)
            FacetKeys.INNHOLD_FRA_FYLKER -> filterWithUnderfacets(FacetKeys.INNHOLD_FRA_FYLKER, fylkeFilters)
            else -> BoolQueryBuilder()
        }
    }

    private fun activePreferredLanguageFilterQuery(preferredLanguage: String): BoolQueryBuilder {
        return BoolQueryBuilder().apply {
            // Ikke vis treff som har en versjon på foretrukket språk
            this.mustNot(TermQueryBuilder(LANGUAGE_REFS, preferredLanguage))

            when (preferredLanguage) {
                NORWEGIAN_BOKMAAL ->
                    // Ikke vis engelsk versjon dersom det finnes en nynorsk-versjon
                    this.mustNot(
                        BoolQueryBuilder()
                            .must(TermQueryBuilder(LANGUAGE, ENGLISH))
                            .must(TermQueryBuilder(LANGUAGE_REFS, NORWEGIAN_NYNORSK))
                    )

                NORWEGIAN_NYNORSK ->
                    // Ikke vis engelsk versjon dersom det finnes en bokmål-versjon
                    this.mustNot(
                        BoolQueryBuilder()
                            .must(TermQueryBuilder(LANGUAGE, ENGLISH))
                            .must(TermQueryBuilder(LANGUAGE_REFS, NORWEGIAN_BOKMAAL))
                    )

                ENGLISH ->
                    // Ikke vis nynorsk-versjon dersom det finnes en engelsk versjon
                    this.mustNot(
                        BoolQueryBuilder()
                            .must(TermQueryBuilder(LANGUAGE, NORWEGIAN_NYNORSK))
                            .must(TermQueryBuilder(LANGUAGE_REFS, NORWEGIAN_BOKMAAL))
                    )
            }
        }
    }
}