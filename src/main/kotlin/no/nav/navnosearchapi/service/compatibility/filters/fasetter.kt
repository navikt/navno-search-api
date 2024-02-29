package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ARBEIDSGIVER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_PRIVATPERSON
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_PRIVATPERSON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_SAMARBEIDSPARTNER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_SAMARBEIDSPARTNER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val fasettFilters = mapOf(
    FASETT_PRIVATPERSON to FilterEntry(
        name = FASETT_PRIVATPERSON_NAME,
        filterQuery = audienceFilter(ValidAudiences.PRIVATPERSON.descriptor)
    ),
    FASETT_ARBEIDSGIVER to FilterEntry(
        name = FASETT_ARBEIDSGIVER_NAME,
        filterQuery = audienceFilter(ValidAudiences.ARBEIDSGIVER.descriptor)
    ),
    FASETT_SAMARBEIDSPARTNER to FilterEntry(
        name = FASETT_SAMARBEIDSPARTNER_NAME,
        filterQuery = audienceFilter(ValidAudiences.SAMARBEIDSPARTNER.descriptor)
    ),
    FASETT_NYHETER to FilterEntry(
        name = FASETT_NYHETER_NAME,
        filterQuery = BoolQueryBuilder()
            .must(
                BoolQueryBuilder()
                    .should(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
                    .should(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            )
            .mustNot(existsQuery(FYLKE))
    ),
    FASETT_STATISTIKK to FilterEntry(
        name = FASETT_STATISTIKK_NAME,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
    ),
    FASETT_ANALYSER_OG_FORSKNING to FilterEntry(
        name = FASETT_ANALYSER_OG_FORSKNING_NAME,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
    ),
    FASETT_INNHOLD_FRA_FYLKER to FilterEntry(
        name = FASETT_INNHOLD_FRA_FYLKER_NAME,
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