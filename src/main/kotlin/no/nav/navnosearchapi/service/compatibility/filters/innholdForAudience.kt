package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.AggregationNames
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.compatibility.utils.UnderFacetNames
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val privatpersonFilters = filtersForAudience(
    audience = ValidAudiences.PRIVATPERSON.descriptor,
    informasjonAggName = AggregationNames.PRIVATPERSON_INFORMASJON,
    kontorAggName = AggregationNames.PRIVATPERSON_KONTOR,
    soknadAggName = AggregationNames.PRIVATPERSON_SOKNAD_OG_SKJEMA,
    aktueltAggName = AggregationNames.PRIVATPERSON_AKTUELT,
)
val arbeidsgiverFilters = filtersForAudience(
    audience = ValidAudiences.ARBEIDSGIVER.descriptor,
    informasjonAggName = AggregationNames.ARBEIDSGIVER_INFORMASJON,
    kontorAggName = AggregationNames.ARBEIDSGIVER_KONTOR,
    soknadAggName = AggregationNames.ARBEIDSGIVER_SOKNAD_OG_SKJEMA,
    aktueltAggName = AggregationNames.ARBEIDSGIVER_AKTUELT,
)
val samarbeidspartnerFilters = filtersForAudience(
    audience = ValidAudiences.SAMARBEIDSPARTNER.descriptor,
    informasjonAggName = AggregationNames.SAMARBEIDSPARTNER_INFORMASJON,
    kontorAggName = AggregationNames.SAMARBEIDSPARTNER_KONTOR,
    soknadAggName = AggregationNames.SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA,
    aktueltAggName = AggregationNames.SAMARBEIDSPARTNER_AKTUELT,
)

private fun filtersForAudience(
    audience: String,
    informasjonAggName: String,
    kontorAggName: String,
    soknadAggName: String,
    aktueltAggName: String
): Map<String, FilterEntry> {
    return mapOf(
        UnderFacetKeys.INFORMASJON to FilterEntry(
            name = UnderFacetNames.INFORMASJON,
            aggregationName = informasjonAggName,
            filterQuery = innholdBaseFilter(audience).must(termQuery(METATAGS, ValidMetatags.INFORMASJON.descriptor))
        ),
        UnderFacetKeys.KONTOR to FilterEntry(
            name = UnderFacetNames.KONTOR,
            aggregationName = kontorAggName,
            filterQuery = innholdBaseFilter(audience = audience).must(
                BoolQueryBuilder()
                    .should(termQuery(TYPE, ValidTypes.KONTOR.descriptor))
                    .should(termQuery(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
            )
        ),
        UnderFacetKeys.SOKNAD_OG_SKJEMA to FilterEntry(
            name = UnderFacetNames.SOKNAD_OG_SKJEMA,
            aggregationName = soknadAggName,
            filterQuery = innholdBaseFilter(audience).must(termQuery(TYPE, ValidTypes.SKJEMA.descriptor))
        ),
        UnderFacetKeys.AKTUELT to FilterEntry(
            name = UnderFacetNames.AKTUELT,
            aggregationName = aktueltAggName,
            filterQuery = innholdBaseFilter(audience = audience).must(
                termQuery(
                    METATAGS,
                    ValidMetatags.NYHET.descriptor
                )
            )
        ),
    )
}

private fun innholdBaseFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .mustNot(existsQuery(FYLKE))
        .must(lenientAudienceFilter(audience))
}

fun lenientAudienceFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .should(TermQueryBuilder(AUDIENCE, audience))
        .should(TermQueryBuilder(AUDIENCE, ValidAudiences.ANDRE.descriptor))
        .should(BoolQueryBuilder().mustNot(existsQuery(AUDIENCE)))
}