package no.nav.navnosearchapi.common.utils

import java.time.ZonedDateTime

fun now() = ZonedDateTime.now()
fun sevenDaysAgo() = ZonedDateTime.now().minusDays(7)
fun thirtyDaysAgo() = ZonedDateTime.now().minusDays(30)
fun twelveMonthsAgo() = ZonedDateTime.now().minusMonths(12)