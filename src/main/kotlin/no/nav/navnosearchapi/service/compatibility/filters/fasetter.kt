package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.FacetNames
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val fasettFilters = mapOf(
    FacetKeys.PRIVATPERSON to FilterEntry(
        name = FacetNames.PRIVATPERSON,
        filterQuery = audienceFilter(ValidAudiences.PRIVATPERSON.descriptor)
    ),
    FacetKeys.ARBEIDSGIVER to FilterEntry(
        name = FacetNames.ARBEIDSGIVER,
        filterQuery = audienceFilter(ValidAudiences.ARBEIDSGIVER.descriptor)
    ),
    FacetKeys.SAMARBEIDSPARTNER to FilterEntry(
        name = FacetNames.SAMARBEIDSPARTNER,
        filterQuery = audienceFilter(ValidAudiences.SAMARBEIDSPARTNER.descriptor)
    ),
    FacetKeys.NYHETER to FilterEntry(
        name = FacetNames.NYHETER,
        filterQuery = BoolQueryBuilder()
            .must(
                BoolQueryBuilder()
                    .should(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
                    .should(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            )
            .mustNot(existsQuery(FYLKE))
    ),
    FacetKeys.STATISTIKK to FilterEntry(
        name = FacetNames.STATISTIKK,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
    ),
    FacetKeys.ANALYSER_OG_FORSKNING to FilterEntry(
        name = FacetNames.ANALYSER_OG_FORSKNING,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
    ),
    FacetKeys.INNHOLD_FRA_FYLKER to FilterEntry(
        name = FacetNames.INNHOLD_FRA_FYLKER,
        filterQuery = BoolQueryBuilder()
            .must(existsQuery(FYLKE))
    )
)

private fun audienceFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .must(lenientAudienceFilter(audience))
        .must(
            BoolQueryBuilder()
                .should(termQuery(METATAGS, ValidMetatags.INFORMASJON.descriptor))
                .should(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
                .should(termQuery(TYPE, ValidTypes.KONTOR.descriptor))
                .should(termQuery(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
                .should(termQuery(TYPE, ValidTypes.SKJEMA.descriptor))
        )
        .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .mustNot(existsQuery(FYLKE))
}