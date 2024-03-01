package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.FacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.FacetNames
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

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
                    .should(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
                    .should(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            )
            .mustNot(ExistsQueryBuilder(FYLKE))
    ),
    FacetKeys.STATISTIKK to FilterEntry(
        name = FacetNames.STATISTIKK,
        filterQuery = BoolQueryBuilder()
            .must(TermQueryBuilder(METATAGS, ValidMetatags.STATISTIKK.descriptor))
            .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
    ),
    FacetKeys.ANALYSER_OG_FORSKNING to FilterEntry(
        name = FacetNames.ANALYSER_OG_FORSKNING,
        filterQuery = BoolQueryBuilder()
            .must(TermQueryBuilder(METATAGS, ValidMetatags.ANALYSE.descriptor))
    ),
    FacetKeys.INNHOLD_FRA_FYLKER to FilterEntry(
        name = FacetNames.INNHOLD_FRA_FYLKER,
        filterQuery = BoolQueryBuilder()
            .must(ExistsQueryBuilder(FYLKE))
    )
)

private fun audienceFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .must(lenientAudienceFilter(audience))
        .must(
            BoolQueryBuilder()
                .should(TermQueryBuilder(METATAGS, ValidMetatags.INFORMASJON.descriptor))
                .should(TermQueryBuilder(METATAGS, ValidMetatags.NYHET.descriptor))
                .should(TermQueryBuilder(TYPE, ValidTypes.KONTOR.descriptor))
                .should(TermQueryBuilder(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
                .should(TermQueryBuilder(TYPE, ValidTypes.SKJEMA.descriptor))
        )
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .mustNot(ExistsQueryBuilder(FYLKE))
}