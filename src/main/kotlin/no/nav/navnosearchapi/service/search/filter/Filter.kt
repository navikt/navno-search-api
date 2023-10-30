package no.nav.navnosearchapi.service.search.filter

import no.nav.navnosearchapi.service.search.existsQuery
import no.nav.navnosearchapi.service.search.rangeQuery
import no.nav.navnosearchapi.service.search.termQuery
import no.nav.navnosearchapi.utils.AUDIENCE
import no.nav.navnosearchapi.utils.FYLKE
import no.nav.navnosearchapi.utils.IS_FILE
import no.nav.navnosearchapi.utils.LANGUAGE
import no.nav.navnosearchapi.utils.LAST_UPDATED
import no.nav.navnosearchapi.utils.METATAGS
import org.opensearch.index.query.BoolQueryBuilder
import java.time.ZonedDateTime

data class Filter(
    val audience: List<String>? = null,
    val language: List<String>? = null,
    val fylke: List<String>? = null,
    val metatags: List<String>? = null,
    val isFile: List<String>? = null,
    val excludeMetatags: List<String>? = null,
    val requiredFields: List<String>? = null,
    val requiredMissingFields: List<String>? = null,
    val lastUpdatedFrom: ZonedDateTime? = null,
    val lastUpdatedTo: ZonedDateTime? = null,
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
            query.must(rangeQuery(LAST_UPDATED, lastUpdatedFrom, lastUpdatedTo))
        }

        return query
    }
}