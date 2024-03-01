package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.HREF
import no.nav.navnosearchapi.service.search.config.EXACT_PHRASE_MATCH_BOOST
import no.nav.navnosearchapi.service.search.config.FUZZY_HIGH_DISTANCE
import no.nav.navnosearchapi.service.search.config.FUZZY_LOW_DISTANCE
import no.nav.navnosearchapi.service.search.config.NGRAM_MIN_LENGTH
import no.nav.navnosearchapi.service.search.config.allTextFields
import no.nav.navnosearchapi.service.search.config.exactInnerFieldsToWeight
import no.nav.navnosearchapi.service.search.config.fieldsToWeight
import no.nav.navnosearchapi.service.search.config.ngramsInnerFieldsToWeight
import no.nav.navnosearchapi.service.search.utils.addIfMultipleWordsInTerm
import no.nav.navnosearchapi.service.search.utils.addIfTermHasMinLength
import org.opensearch.common.unit.Fuzziness
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.DisMaxQueryBuilder
import org.opensearch.index.query.MatchQueryBuilder
import org.opensearch.index.query.MultiMatchQueryBuilder
import org.opensearch.index.query.Operator
import org.opensearch.index.query.QueryBuilder

fun searchAllTextQuery(term: String, skjemanummer: String? = null): QueryBuilder {
    return BoolQueryBuilder()
        // Filter (bidrar ikke til score) - alle treff må inneholde alle søkeord (på tvers av feltene)
        .filter(
            MultiMatchQueryBuilder(term)
                .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                .operator(Operator.AND)
                .apply { allTextFields.forEach { this.field(it) } }
        )
        .should(
            // Bruk subquery med høyest score
            DisMaxQueryBuilder()
                // Standard søk i title, ingress og text
                .add(
                    MultiMatchQueryBuilder(term)
                        .fields(fieldsToWeight)
                        .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                )
                // Ngram-søk i title og ingress (kun dersom søketerm er minst 4 tegn)
                .addIfTermHasMinLength(
                    term,
                    NGRAM_MIN_LENGTH,
                    MultiMatchQueryBuilder(term)
                        .fields(ngramsInnerFieldsToWeight)
                        .operator(Operator.AND)
                )
                // Eksakt frase-søk (kun ved flere søkeord)
                .addIfMultipleWordsInTerm(
                    term,
                    searchAllTextForPhraseQuery(term)
                        .boost(EXACT_PHRASE_MATCH_BOOST)
                )
        ).apply { skjemanummer?.let { this.must(searchAllTextForPhraseQuery(it)) } }
}

fun searchAllTextForPhraseQuery(term: String): QueryBuilder {
    return MultiMatchQueryBuilder(term)
        .fields(exactInnerFieldsToWeight)
        .type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun searchUrlQuery(term: String): QueryBuilder {
    return MatchQueryBuilder(HREF, term).fuzziness(Fuzziness.AUTO)
}
