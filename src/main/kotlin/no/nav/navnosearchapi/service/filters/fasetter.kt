package no.nav.navnosearchapi.service.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.utils.FacetKeys
import no.nav.navnosearchapi.service.utils.FacetNames
import no.nav.navnosearchapi.service.utils.joinClausesToSingleQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val fasettFilters = mapOf(
    FacetKeys.PRIVATPERSON to FilterEntry(
        name = FacetNames.PRIVATPERSON,
        filterQuery = joinClausesToSingleQuery(shouldClauses = privatpersonFilters.values.map { it.filterQuery })
    ),
    FacetKeys.ARBEIDSGIVER to FilterEntry(
        name = FacetNames.ARBEIDSGIVER,
        filterQuery = joinClausesToSingleQuery(shouldClauses = arbeidsgiverFilters.values.map { it.filterQuery })
    ),
    FacetKeys.SAMARBEIDSPARTNER to FilterEntry(
        name = FacetNames.SAMARBEIDSPARTNER,
        filterQuery = joinClausesToSingleQuery(shouldClauses = samarbeidspartnerFilters.values.map { it.filterQuery })
    ),
    FacetKeys.PRESSE to FilterEntry(
        name = FacetNames.PRESSE,
        filterQuery = BoolQueryBuilder()
            .must(
                BoolQueryBuilder()
                    .should(TermQueryBuilder(METATAGS, ValidMetatags.PRESSE.descriptor))
                    .should(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            )
            .mustNot(ExistsQueryBuilder(FYLKE))
    ),
    FacetKeys.STATISTIKK to FilterEntry(
        name = FacetNames.STATISTIKK,
        filterQuery = BoolQueryBuilder().must(TermQueryBuilder(METATAGS, ValidMetatags.STATISTIKK.descriptor))
    ),
    FacetKeys.ANALYSER_OG_FORSKNING to FilterEntry(
        name = FacetNames.ANALYSER_OG_FORSKNING,
        filterQuery = BoolQueryBuilder().must(TermQueryBuilder(METATAGS, ValidMetatags.ANALYSE.descriptor))
    ),
    FacetKeys.INNHOLD_FRA_FYLKER to FilterEntry(
        name = FacetNames.INNHOLD_FRA_FYLKER,
        filterQuery = BoolQueryBuilder().must(ExistsQueryBuilder(FYLKE))
    )
)