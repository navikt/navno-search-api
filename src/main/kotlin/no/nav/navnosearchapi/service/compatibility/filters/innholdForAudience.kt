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
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val privatpersonFilters = filtersForAudience(
    audience = ValidAudiences.PERSON.descriptor,
    informasjonAggName = AggregationNames.PRIVATPERSON_INFORMASJON,
    kontorAggName = AggregationNames.PRIVATPERSON_KONTOR,
    soknadAggName = AggregationNames.PRIVATPERSON_SOKNAD_OG_SKJEMA,
    aktueltAggName = AggregationNames.PRIVATPERSON_AKTUELT,
)
val arbeidsgiverFilters = filtersForAudience(
    audience = ValidAudiences.EMPLOYER.descriptor,
    informasjonAggName = AggregationNames.ARBEIDSGIVER_INFORMASJON,
    kontorAggName = AggregationNames.ARBEIDSGIVER_KONTOR,
    soknadAggName = AggregationNames.ARBEIDSGIVER_SOKNAD_OG_SKJEMA,
    aktueltAggName = AggregationNames.ARBEIDSGIVER_AKTUELT,
)
val samarbeidspartnerFilters = filtersForAudience(
    audience = ValidAudiences.PROVIDER.descriptor,
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
            filterQuery = innholdBaseFilter(audience).must(
                TermQueryBuilder(
                    METATAGS,
                    ValidMetatags.INFORMASJON.descriptor
                )
            )
        ),
        UnderFacetKeys.KONTOR to FilterEntry(
            name = UnderFacetNames.KONTOR,
            aggregationName = kontorAggName,
            filterQuery = innholdBaseFilter(audience = audience).must(
                BoolQueryBuilder()
                    .should(TermQueryBuilder(TYPE, ValidTypes.KONTOR.descriptor))
                    .should(TermQueryBuilder(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
            )
        ),
        UnderFacetKeys.SOKNAD_OG_SKJEMA to FilterEntry(
            name = UnderFacetNames.SOKNAD_OG_SKJEMA,
            aggregationName = soknadAggName,
            filterQuery = innholdBaseFilter(audience).must(
                BoolQueryBuilder()
                    .should(TermQueryBuilder(TYPE, ValidTypes.SKJEMA.descriptor))
                    .should(TermQueryBuilder(TYPE, ValidTypes.SKJEMAOVERSIKT.descriptor))
            )
        ),
        UnderFacetKeys.AKTUELT to FilterEntry(
            name = UnderFacetNames.AKTUELT,
            aggregationName = aktueltAggName,
            filterQuery = innholdBaseFilter(audience = audience, isAudienceStrict = true).must(
                TermQueryBuilder(
                    METATAGS,
                    ValidMetatags.NYHET.descriptor
                )
            )
        ),
    )
}

private fun innholdBaseFilter(audience: String, isAudienceStrict: Boolean = false): BoolQueryBuilder {
    return BoolQueryBuilder()
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(TermQueryBuilder(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .mustNot(ExistsQueryBuilder(FYLKE))
        .must(if (isAudienceStrict) strictAudienceFilter(audience) else lenientAudienceFilter(audience))
}

private fun lenientAudienceFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .should(TermQueryBuilder(AUDIENCE, audience))
        .should(TermQueryBuilder(AUDIENCE, ValidAudiences.OTHER.descriptor))
        .should(BoolQueryBuilder().mustNot(ExistsQueryBuilder(AUDIENCE)))
}

private fun strictAudienceFilter(audience: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .should(TermQueryBuilder(AUDIENCE, audience))
        .should(TermQueryBuilder(AUDIENCE, ValidAudiences.OTHER.descriptor))
}