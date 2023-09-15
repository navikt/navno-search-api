package no.nav.navnosearchapi.search.service.search

import no.nav.navnosearchapi.common.utils.AUDIENCE
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.common.utils.IS_FILE
import no.nav.navnosearchapi.common.utils.LANGUAGE
import no.nav.navnosearchapi.common.utils.LAST_UPDATED
import no.nav.navnosearchapi.common.utils.METATAGS
import org.opensearch.index.query.QueryBuilder
import java.time.LocalDateTime
import java.time.ZoneId

data class Filters(
    val audience: List<String>? = null,
    val language: List<String>? = null,
    val fylke: List<String>? = null,
    val metatags: List<String>? = null,
    val isFile: List<String>? = null,
    val lastUpdatedFrom: LocalDateTime? = null,
    val lastUpdatedTo: LocalDateTime? = null,
) {
    fun toQueryList(): List<QueryBuilder> {
        val filterList = mutableListOf<QueryBuilder>()

        audience?.let { filterList.add(termsQuery(AUDIENCE, it)) }
        language?.let { filterList.add(termsQuery(LANGUAGE, it)) }
        fylke?.let { filterList.add(termsQuery(FYLKE, it)) }
        metatags?.let { filterList.add(termsQuery(METATAGS, it)) }
        isFile?.let { filterList.add(termsQuery(IS_FILE, it)) }

        if (lastUpdatedFrom != null || lastUpdatedTo != null) {
            filterList.add(
                rangeQuery(
                    LAST_UPDATED,
                    lastUpdatedFrom?.atZone(ZoneId.systemDefault()),
                    lastUpdatedTo?.atZone(ZoneId.systemDefault())
                )
            )
        }

        return filterList
    }
}