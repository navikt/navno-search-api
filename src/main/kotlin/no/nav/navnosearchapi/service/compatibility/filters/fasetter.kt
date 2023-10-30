package no.nav.navnosearchapi.service.compatibility.filters

import joinClausesToSingleQuery
import no.nav.navnosearchapi.enums.ValidMetatags
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ENGLISH
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ENGLISH_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_FILER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_FILER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_FRA_FYLKER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_INNHOLD_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_NYHETER_NAME
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_STATISTIKK_NAME
import no.nav.navnosearchapi.service.search.filter.Filter
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.FYLKE

val fasettFilters = mapOf(
    FASETT_INNHOLD to FilterEntry(
        name = FASETT_INNHOLD_NAME,
        filterQuery = joinClausesToSingleQuery(shouldClauses = innholdFilters.values.map { it.filterQuery })
    ),
    FASETT_ENGLISH to FilterEntry(
        name = FASETT_ENGLISH_NAME,
        filterQuery = Filter(language = listOf(ENGLISH)).toQuery()
    ),
    FASETT_NYHETER to FilterEntry(
        name = FASETT_NYHETER_NAME,
        filterQuery = Filter(
            metatags = listOf(ValidMetatags.NYHET.descriptor),
            isFile = listOf(false.toString())
        ).toQuery()
    ),
    FASETT_ANALYSER_OG_FORSKNING to FilterEntry(
        name = FASETT_ANALYSER_OG_FORSKNING_NAME,
        filterQuery = Filter(
            metatags = listOf(ValidMetatags.ANALYSE.descriptor),
            isFile = listOf(false.toString())
        ).toQuery()
    ),
    FASETT_STATISTIKK to FilterEntry(
        name = FASETT_STATISTIKK_NAME,
        filterQuery = Filter(
            metatags = listOf(ValidMetatags.STATISTIKK.descriptor),
            excludeMetatags = listOf(ValidMetatags.NYHET.descriptor),
            isFile = listOf(false.toString())
        ).toQuery()
    ),
    FASETT_INNHOLD_FRA_FYLKER to FilterEntry(
        name = FASETT_INNHOLD_FRA_FYLKER_NAME,
        filterQuery = Filter(
            isFile = listOf(false.toString()),
            requiredFields = listOf(FYLKE)
        ).toQuery()
    ),
    FASETT_FILER to FilterEntry(
        name = FASETT_FILER_NAME,
        filterQuery = Filter(
            isFile = listOf(true.toString())
        ).toQuery()
    ),
)