package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.IS_FILE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
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
        filterQuery = innholdFilter(ValidMetatags.INFORMASJON.descriptor)
    ),
    UNDERFASETT_KONTOR to FilterEntry(
        name = UNDERFASETT_KONTOR_NAME,
        filterQuery = innholdFilter(ValidMetatags.KONTOR.descriptor)
    ),
    UNDERFASETT_SOKNAD_OG_SKJEMA to FilterEntry(
        name = UNDERFASETT_SOKNAD_OG_SKJEMA_NAME,
        filterQuery = innholdFilter(ValidMetatags.SKJEMA.descriptor)
    ),
)

private fun innholdFilter(requiredMetatag: String): BoolQueryBuilder {
    return BoolQueryBuilder()
        .must(termQuery(METATAGS, requiredMetatag))
        .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
        .mustNot(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
        .must(termQuery(IS_FILE, false.toString()))
        .mustNot(existsQuery(FYLKE))
}