package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_INFORMASJON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_KONTOR_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val innholdFilters = mapOf(
    UNDERFASETT_INFORMASJON to FilterEntry(
        name = UNDERFASETT_INFORMASJON_NAME,
        filterQuery = innholdBaseFilter().must(termQuery(METATAGS, ValidMetatags.INFORMASJON.descriptor))
    ),
    UNDERFASETT_KONTOR to FilterEntry(
        name = UNDERFASETT_KONTOR_NAME,
        filterQuery = innholdBaseFilter().must(
            BoolQueryBuilder()
                .should(termQuery(TYPE, ValidTypes.KONTOR.descriptor))
                .should(termQuery(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
        )
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
        name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME,
        filterQuery = innholdBaseFilter().must(termQuery(TYPE, ValidTypes.SKJEMA.descriptor))
    ),
)

private fun innholdBaseFilter(): BoolQueryBuilder {
    return BoolQueryBuilder()
        .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .mustNot(existsQuery(FYLKE))
}