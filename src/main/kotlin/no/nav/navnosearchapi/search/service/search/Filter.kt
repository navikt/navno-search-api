package no.nav.navnosearchapi.search.service.search

import no.nav.navnosearchapi.common.utils.AUDIENCE
import no.nav.navnosearchapi.common.utils.FYLKE
import no.nav.navnosearchapi.common.utils.IS_FILE
import no.nav.navnosearchapi.common.utils.LANGUAGE
import no.nav.navnosearchapi.common.utils.LAST_UPDATED
import no.nav.navnosearchapi.common.utils.METATAGS
import org.opensearch.index.query.BoolQueryBuilder
import java.time.LocalDateTime
import java.time.ZoneId

data class Filter(
    val audience: List<String>? = null,
    val language: List<String>? = null,
    val fylke: List<String>? = null,
    val metatags: List<String>? = null,
    val isFile: List<String>? = null,
    val excludeMetatags: List<String>? = null,
    val requiredFields: List<String>? = null,
    val requiredMissingFields: List<String>? = null,
    val lastUpdatedFrom: LocalDateTime? = null,
    val lastUpdatedTo: LocalDateTime? = null,
) {
    fun toQuery(): BoolQueryBuilder {
        val query = BoolQueryBuilder()

        audience?.let { it.forEach { term -> query.must(termQuery(AUDIENCE, term)) } }
        language?.let { it.forEach { term -> query.must(termQuery(LANGUAGE, term)) } }
        fylke?.let { it.forEach { term -> query.must(termQuery(FYLKE, term)) } }
        metatags?.let { it.forEach { term -> query.must(termQuery(METATAGS, term)) } }
        isFile?.let { it.forEach { term -> query.must(termQuery(IS_FILE, term)) } }

        excludeMetatags?.let { it.forEach { term -> query.mustNot(termQuery(METATAGS, term)) } }
        requiredFields?.let { it.forEach { field -> query.must(existsQuery(field)) } }
        requiredMissingFields?.let { it.forEach { field -> query.mustNot(existsQuery(field)) } }

        if (lastUpdatedFrom != null || lastUpdatedTo != null) {
            query.must(
                rangeQuery(
                    LAST_UPDATED,
                    lastUpdatedFrom?.atZone(ZoneId.systemDefault()),
                    lastUpdatedTo?.atZone(ZoneId.systemDefault())
                )
            )
        }

        return query
    }
}