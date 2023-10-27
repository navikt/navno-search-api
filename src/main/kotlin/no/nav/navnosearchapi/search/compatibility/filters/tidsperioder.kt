package no.nav.navnosearchapi.search.compatibility.filters

import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_12_MONTHS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_30_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_LAST_7_DAYS
import no.nav.navnosearchapi.common.utils.DATE_RANGE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.common.utils.now
import no.nav.navnosearchapi.common.utils.sevenDaysAgo
import no.nav.navnosearchapi.common.utils.thirtyDaysAgo
import no.nav.navnosearchapi.common.utils.twelveMonthsAgo
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_ALL_DATES
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_12_MONTHS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_30_DAYS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_LAST_7_DAYS
import no.nav.navnosearchapi.search.compatibility.utils.TIDSPERIODE_OLDER_THAN_12_MONTHS
import no.nav.navnosearchapi.search.search.filter.Filter


val tidsperiodeFilters = mapOf(
    TIDSPERIODE_ALL_DATES to FilterEntry(
        name = TIDSPERIODE_ALL_DATES,
        filterQuery = Filter().toQuery()
    ),
    TIDSPERIODE_OLDER_THAN_12_MONTHS to FilterEntry(
        name = DATE_RANGE_OLDER_THAN_12_MONTHS,
        filterQuery = Filter(lastUpdatedTo = twelveMonthsAgo()).toQuery()
    ),
    TIDSPERIODE_LAST_12_MONTHS to FilterEntry(
        name = DATE_RANGE_LAST_12_MONTHS,
        filterQuery = Filter(lastUpdatedFrom = twelveMonthsAgo(), lastUpdatedTo = now()).toQuery()
    ),
    TIDSPERIODE_LAST_30_DAYS to FilterEntry(
        name = DATE_RANGE_LAST_30_DAYS,
        filterQuery = Filter(lastUpdatedFrom = thirtyDaysAgo(), lastUpdatedTo = now()).toQuery()
    ),
    TIDSPERIODE_LAST_7_DAYS to FilterEntry(
        name = DATE_RANGE_LAST_7_DAYS,
        filterQuery = Filter(lastUpdatedFrom = sevenDaysAgo(), lastUpdatedTo = now()).toQuery()
    ),
)