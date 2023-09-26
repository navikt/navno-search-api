package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ENGLISH_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_FILER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_NYHETER_NAME
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.service.search.Filter
import org.opensearch.index.query.ExistsQueryBuilder

val fasettFilters = mapOf(
    FASETT_INNHOLD to FilterEntry(
        name = FASETT_INNHOLD_NAME,
        filters = innholdFilters[UNDERFASETT_INFORMASJON]!!.filters + innholdFilters[UNDERFASETT_KONTOR]!!.filters + innholdFilters[UNDERFASETT_SOKNAD_OG_SKJEMA]!!.filters
    ),
    FASETT_ENGLISH to FilterEntry(name = FASETT_ENGLISH_NAME, filters = listOf(Filter(language = listOf(ENGLISH)).toQuery())),
    FASETT_NYHETER to FilterEntry(
        name = FASETT_NYHETER_NAME,
        filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_ANALYSER_OG_FORSKNING to FilterEntry(
        name = FASETT_ANALYSER_OG_FORSKNING_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.ANALYSE.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_STATISTIKK to FilterEntry(
        name = FASETT_STATISTIKK_NAME, filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.STATISTIKK.descriptor),
                excludeMetatags = listOf(ValidMetatags.NYHET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_INNHOLD_FRA_FYLKER to FilterEntry(
        name = FASETT_INNHOLD_FRA_FYLKER_NAME, filters = listOf(
            Filter(isFile = listOf(false.toString())).toQuery(),
            ExistsQueryBuilder(FYLKE)
        )
    ),
    FASETT_FILER to FilterEntry(
        name = FASETT_FILER_NAME, filters = listOf(
            Filter(
                isFile = listOf(true.toString())
            ).toQuery()
        )
    ),
)