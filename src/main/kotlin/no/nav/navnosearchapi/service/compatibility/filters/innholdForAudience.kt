package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.ARBEIDSGIVER_AKTUELT_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.ARBEIDSGIVER_INFORMASJON_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.ARBEIDSGIVER_KONTOR_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.ARBEIDSGIVER_SOKNAD_OG_SKJEMA_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.PRIVATPERSON_AKTUELT_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.PRIVATPERSON_INFORMASJON_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.PRIVATPERSON_KONTOR_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.PRIVATPERSON_SOKNAD_OG_SKJEMA_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.SAMARBEIDSPARTNER_AKTUELT_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.SAMARBEIDSPARTNER_INFORMASJON_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.SAMARBEIDSPARTNER_KONTOR_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA_AGG_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AKTUELT
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_AKTUELT_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.TermQueryBuilder

val privatpersonFilters = filtersForAudience(
    audience = ValidAudiences.PRIVATPERSON.descriptor,
    informasjonAggName = PRIVATPERSON_INFORMASJON_AGG_NAME,
    kontorAggName = PRIVATPERSON_KONTOR_AGG_NAME,
    soknadAggName = PRIVATPERSON_SOKNAD_OG_SKJEMA_AGG_NAME,
    aktueltAggName = PRIVATPERSON_AKTUELT_AGG_NAME,
)
val arbeidsgiverFilters = filtersForAudience(
    audience = ValidAudiences.ARBEIDSGIVER.descriptor,
    informasjonAggName = ARBEIDSGIVER_INFORMASJON_AGG_NAME,
    kontorAggName = ARBEIDSGIVER_KONTOR_AGG_NAME,
    soknadAggName = ARBEIDSGIVER_SOKNAD_OG_SKJEMA_AGG_NAME,
    aktueltAggName = ARBEIDSGIVER_AKTUELT_AGG_NAME,
)
val samarbeidspartnerFilters = filtersForAudience(
    audience = ValidAudiences.SAMARBEIDSPARTNER.descriptor,
    informasjonAggName = SAMARBEIDSPARTNER_INFORMASJON_AGG_NAME,
    kontorAggName = SAMARBEIDSPARTNER_KONTOR_AGG_NAME,
    soknadAggName = SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA_AGG_NAME,
    aktueltAggName = SAMARBEIDSPARTNER_AKTUELT_AGG_NAME,
)

private fun filtersForAudience(
    audience: String,
    informasjonAggName: String,
    kontorAggName: String,
    soknadAggName: String,
    aktueltAggName: String
): Map<String, FilterEntry> {
    return mapOf(
        UNDERFASETT_INFORMASJON to FilterEntry(
            name = UNDERFASETT_INFORMASJON_NAME,
            aggregationName = informasjonAggName,
            filterQuery = innholdBaseFilter(audience).must(termQuery(METATAGS, ValidMetatags.INFORMASJON.descriptor))
        ),
        UNDERFASETT_KONTOR to FilterEntry(
            name = UNDERFASETT_KONTOR_NAME,
            aggregationName = kontorAggName,
            filterQuery = innholdBaseFilter(audience = audience).must(
                BoolQueryBuilder()
                    .should(termQuery(TYPE, ValidTypes.KONTOR.descriptor))
                    .should(termQuery(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
            )
        ),
        UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
            name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME,
            aggregationName = soknadAggName,
            filterQuery = innholdBaseFilter(audience).must(termQuery(TYPE, ValidTypes.SKJEMA.descriptor))
        ),
        UNDERFASETT_AKTUELT to FilterEntry(
            name = UNDERFASETT_AKTUELT_NAME,
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