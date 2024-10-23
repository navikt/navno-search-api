package no.nav.navnosearchapi.service.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE_REFS
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchapi.service.filters.FilterEntry
import no.nav.navnosearchapi.service.filters.analyseFilters
import no.nav.navnosearchapi.service.filters.arbeidsgiverFilters
import no.nav.navnosearchapi.service.filters.fasettFilters
import no.nav.navnosearchapi.service.filters.fylkeFilters
import no.nav.navnosearchapi.service.filters.privatpersonFilters
import no.nav.navnosearchapi.service.filters.samarbeidspartnerFilters
import no.nav.navnosearchapi.service.filters.statistikkFilters
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

fun activeFasettFilterQuery(f: String, uf: List<String>): BoolQueryBuilder {
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

fun activePreferredLanguageFilterQuery(preferredLanguage: String): BoolQueryBuilder {
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
                // Ikke vis nynorsk-versjon dersom det finnes en bokmål-versjon
                this.mustNot(
                    BoolQueryBuilder()
                        .must(TermQueryBuilder(LANGUAGE, NORWEGIAN_NYNORSK))
                        .must(TermQueryBuilder(LANGUAGE_REFS, NORWEGIAN_BOKMAAL))
                )
        }
    }
}

fun joinClausesToSingleQuery(
    shouldClauses: List<BoolQueryBuilder> = emptyList(),
    mustClauses: List<BoolQueryBuilder> = emptyList()
): BoolQueryBuilder {
    val joinedQuery = BoolQueryBuilder()
    shouldClauses.forEach { joinedQuery.should(it) }
    mustClauses.forEach { joinedQuery.must(it) }
    return joinedQuery
}