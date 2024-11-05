package no.nav.navnosearchapi.search.utils

import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE_REFS
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchapi.search.filters.Filter
import no.nav.navnosearchapi.search.filters.facets.fasettFilters
import no.nav.navnosearchapi.search.filters.joinToSingleQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

fun activeFasettFilterQuery(f: String, uf: List<String>): BoolQueryBuilder {
    return fasettFilters.find { it.key == f }!!.let { facet ->
        when {
            uf.isEmpty() -> facet.filterQuery
            else -> facet.underFacets
                .filter { it.key in uf }
                .map(Filter::filterQuery)
                .joinToSingleQuery(BoolQueryBuilder::should)
        }
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