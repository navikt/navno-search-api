package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.search.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_INFORMASJON
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_KONTOR
import no.nav.navnosearchapi.search.compatibility.utils.UNDERFASETT_SOKNAD_OG_SKJEMA
import no.nav.navnosearchapi.search.service.search.Filter
import org.opensearch.index.query.ExistsQueryBuilder

val fasettFilters = mapOf(
    FASETT_INNHOLD to FilterEntry(
        name = "Innhold",
        filters = underfasettFilters[UNDERFASETT_INFORMASJON]!!.filters + underfasettFilters[UNDERFASETT_KONTOR]!!.filters + underfasettFilters[UNDERFASETT_SOKNAD_OG_SKJEMA]!!.filters
    ),
    FASETT_ENGLISH to FilterEntry(name = "English", filters = listOf(Filter(language = listOf(ENGLISH)).toQuery())),
    FASETT_NYHETER to FilterEntry(
        name = "Nyheter",
        filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.NYHET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_ANALYSER_OG_FORSKNING to FilterEntry(
        name = "Analyser og forskning", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.ANALYSE.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_STATISTIKK to FilterEntry(
        name = "Statistikk", filters = listOf(
            Filter(
                metatags = listOf(ValidMetatags.STATISTIKK.descriptor),
                excludeMetatags = listOf(ValidMetatags.NYHET.descriptor),
                isFile = listOf(false.toString())
            ).toQuery()
        )
    ),
    FASETT_INNHOLD_FRA_FYLKER to FilterEntry(
        name = "Innhold fra fylker", filters = listOf(
            Filter(isFile = listOf(false.toString())).toQuery(),
            ExistsQueryBuilder(FYLKE)
        )
    ),
    FASETT_FILER to FilterEntry(
        name = "Filer", filters = listOf(
            Filter(
                isFile = listOf(true.toString())
            ).toQuery()
        )
    ),
)