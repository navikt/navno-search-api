package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING
import no.nav.navnosearchapi.service.compatibility.utils.FASETT_ANALYSER_OG_FORSKNING_NAME
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
import no.nav.navnosearchapi.service.search.queries.existsQuery
import no.nav.navnosearchapi.service.search.queries.termQuery
import org.opensearch.index.query.BoolQueryBuilder

val fasettFilters = mapOf(
    FASETT_INNHOLD to FilterEntry(
        name = FASETT_INNHOLD_NAME,
        filterQuery = BoolQueryBuilder()
            .must(
                BoolQueryBuilder()
                    .should(termQuery(METATAGS, ValidMetatags.INFORMASJON.descriptor))
                    .should(termQuery(TYPE, ValidTypes.KONTOR.descriptor))
                    .should(termQuery(TYPE, ValidTypes.KONTOR_LEGACY.descriptor))
                    .should(termQuery(TYPE, ValidTypes.SKJEMA.descriptor))
            )
            .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
            .mustNot(isFileFilter())
            .mustNot(existsQuery(FYLKE))
    ),
    FASETT_NYHETER to FilterEntry(
        name = FASETT_NYHETER_NAME,
        filterQuery = BoolQueryBuilder()
            .must(
                BoolQueryBuilder()
                    .should(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
                    .should(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            )
            .mustNot(isFileFilter())
            .mustNot(existsQuery(FYLKE))
    ),
    FASETT_ANALYSER_OG_FORSKNING to FilterEntry(
        name = FASETT_ANALYSER_OG_FORSKNING_NAME,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.ANALYSE.descriptor))
            .mustNot(isFileFilter())
    ),
    FASETT_STATISTIKK to FilterEntry(
        name = FASETT_STATISTIKK_NAME,
        filterQuery = BoolQueryBuilder()
            .must(termQuery(METATAGS, ValidMetatags.STATISTIKK.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.NYHET.descriptor))
            .mustNot(termQuery(METATAGS, ValidMetatags.PRESSEMELDING.descriptor))
            .mustNot(isFileFilter())
    ),
    FASETT_INNHOLD_FRA_FYLKER to FilterEntry(
        name = FASETT_INNHOLD_FRA_FYLKER_NAME,
        filterQuery = BoolQueryBuilder()
            .must(existsQuery(FYLKE))
            .mustNot(isFileFilter())
    ),
    FASETT_FILER to FilterEntry(
        name = FASETT_FILER_NAME,
        filterQuery = isFileFilter()
    ),
)

fun isFileFilter(): BoolQueryBuilder = BoolQueryBuilder()
    .should(termQuery(TYPE, ValidTypes.FIL_DOCUMENT.descriptor))
    .should(termQuery(TYPE, ValidTypes.FIL_SPREADSHEET.descriptor))
    .should(termQuery(TYPE, ValidTypes.FIL_ANDRE.descriptor))