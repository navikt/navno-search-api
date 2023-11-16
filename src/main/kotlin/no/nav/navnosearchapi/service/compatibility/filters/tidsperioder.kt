package no.nav.navnosearchapi.service.compatibility.filters

import no.nav.navnosearchadminapi.common.constants.CREATED_AT
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchadminapi.common.constants.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_ALL_DATES
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_12_MONTHS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_LAST_7_DAYS
import no.nav.navnosearchapi.service.compatibility.utils.TIDSPERIODE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.service.search.rangeQuery
import no.nav.navnosearchapi.utils.now
import no.nav.navnosearchapi.utils.sevenDaysAgo
import no.nav.navnosearchapi.utils.thirtyDaysAgo
import no.nav.navnosearchapi.utils.twelveMonthsAgo
import org.opensearch.index.query.BoolQueryBuilder


val tidsperiodeFilters = mapOf(
    TIDSPERIODE_ALL_DATES to FilterEntry(
        name = TIDSPERIODE_ALL_DATES,
        filterQuery = BoolQueryBuilder()
    ),
    TIDSPERIODE_OLDER_THAN_12_MONTHS to FilterEntry(
        name = DATE_RANGE_OLDER_THAN_12_MONTHS,
        filterQuery = BoolQueryBuilder().must(rangeQuery(field = CREATED_AT, lte = twelveMonthsAgo())),
    ),
    TIDSPERIODE_LAST_12_MONTHS to FilterEntry(
        name = DATE_RANGE_LAST_12_MONTHS,
        filterQuery = BoolQueryBuilder().must(rangeQuery(field = CREATED_AT, gte = twelveMonthsAgo(), lte = now())),
    ),
    TIDSPERIODE_LAST_30_DAYS to FilterEntry(
        name = DATE_RANGE_LAST_30_DAYS,
        filterQuery = BoolQueryBuilder().must(rangeQuery(field = CREATED_AT, gte = thirtyDaysAgo(), lte = now())),
    ),
    TIDSPERIODE_LAST_7_DAYS to FilterEntry(
        name = DATE_RANGE_LAST_7_DAYS,
        filterQuery = BoolQueryBuilder().must(rangeQuery(field = CREATED_AT, gte = sevenDaysAgo(), lte = now())),
    ),
)