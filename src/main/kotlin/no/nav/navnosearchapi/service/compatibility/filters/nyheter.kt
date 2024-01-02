package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.ENGLISH
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ARBEIDSGIVER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ARBEIDSGIVER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ENGLISH_NEWS
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_ENGLISH_NEWS_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_NAV_OG_SAMFUNN_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSEMELDINGER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRESSE_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRIVATPERSON
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_PRIVATPERSON_NAME
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.UNDERFASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val nyheterFilters = mapOf(
    UNDERFASETT_PRIVATPERSON to FilterEntry(
        name = UNDERFASETT_PRIVATPERSON_NAME,
        filterQuery = nyhetFilter(
            requiredMetatags = listOf(ValidMetatags.NYHET.descriptor),
            requiredAudience = ValidAudiences.PRIVATPERSON.descriptor
        ),
    ),
    UNDERFASETT_ARBEIDSGIVER to FilterEntry(
        name = UNDERFASETT_ARBEIDSGIVER_NAME,
        filterQuery = nyhetFilter(
            requiredMetatags = listOf(ValidMetatags.NYHET.descriptor),
            requiredAudience = ValidAudiences.ARBEIDSGIVER.descriptor
        ),
    ),
    UNDERFASETT_STATISTIKK to FilterEntry(
        name = UNDERFASETT_STATISTIKK_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.NYHET.descriptor, ValidMetatags.STATISTIKK.descriptor)),
    ),
    UNDERFASETT_PRESSE to FilterEntry(
        name = UNDERFASETT_PRESSE_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.PRESSE.descriptor)),
    ),
    UNDERFASETT_PRESSEMELDINGER to FilterEntry(
        name = UNDERFASETT_PRESSEMELDINGER_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.PRESSEMELDING.descriptor)),
    ),
    UNDERFASETT_NAV_OG_SAMFUNN to FilterEntry(
        name = UNDERFASETT_NAV_OG_SAMFUNN_NAME,
        filterQuery = nyhetFilter(listOf(ValidMetatags.NAV_OG_SAMFUNN.descriptor)),
    ),
    UNDERFASETT_ENGLISH_NEWS to FilterEntry(
        name = UNDERFASETT_ENGLISH_NEWS_NAME,
        filterQuery = nyhetFilter(
            requiredMetatags = listOf(ValidMetatags.NYHET.descriptor),
            requiredLanguage = ENGLISH
        ),
    ),
)

private fun nyhetFilter(
    requiredMetatags: List<String>,
    requiredAudience: String? = null,
    requiredLanguage: String? = null,
): BoolQueryBuilder {
    val query = BoolQueryBuilder()
        .mustNot(isFileFilter())
        .mustNot(existsQuery(FYLKE))

    requiredMetatags.forEach { query.must(termQuery(METATAGS, it)) }
    requiredAudience?.let { query.must(termQuery(AUDIENCE, it)) }
    requiredLanguage?.let { query.must(termQuery(LANGUAGE, it)) }

    return query
}