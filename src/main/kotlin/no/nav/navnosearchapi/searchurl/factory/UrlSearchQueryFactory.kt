package no.nav.navnosearchapi.searchurl.factory

import no.nav.navnosearchadminapi.common.constants.HREF
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchapi.common.utils.applyWeighting
import no.nav.navnosearchapi.search.config.metatagToWeight
import no.nav.navnosearchapi.search.config.typeToWeight
import org.opensearch.common.unit.Fuzziness
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.MatchQueryBuilder
import org.springframework.stereotype.Component

@Component
class UrlSearchQueryFactory {
    fun createBuilder(term: String): NativeSearchQueryBuilder {
        return NativeSearchQueryBuilder().apply {
            withQuery(
                MatchQueryBuilder(HREF, term).fuzziness(Fuzziness.AUTO)
                    .applyWeighting(TYPE, typeToWeight)
                    .applyWeighting(METATAGS, metatagToWeight)
            )
        }
    }
}