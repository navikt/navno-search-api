package no.nav.navnosearchapi.client.queries

import no.nav.navnosearchadminapi.common.constants.HREF
import no.nav.navnosearchapi.client.SearchClient
import no.nav.navnosearchapi.client.config.EXACT_PHRASE_MATCH_BOOST
import no.nav.navnosearchapi.client.config.FUZZY_HIGH_DISTANCE
import no.nav.navnosearchapi.client.config.FUZZY_LOW_DISTANCE
import no.nav.navnosearchapi.client.config.NGRAM_MIN_LENGTH
import no.nav.navnosearchapi.client.config.allTextFields
import no.nav.navnosearchapi.client.config.exactInnerFieldsToWeight
import no.nav.navnosearchapi.client.config.fieldsToWeight
import no.nav.navnosearchapi.client.config.ngramsInnerFieldsToWeight
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
        .filter(containsAllWordsInTermQuery(term))
        .should(
            // Bruker subquery med høyest score
            DisMaxQueryBuilder().apply {
                add(standardSearchQuery(term))
                if (term.trim().length >= NGRAM_MIN_LENGTH) add(ngramSearchQuery(term))
                if (term.split(SearchClient.whitespace).size > 1) add(exactPhraseSearchQuery(term))
            }
        ).apply {
            // Krever ekstakt match på skjemanummer dersom det er satt
            skjemanummer?.let { this.must(searchAllTextForPhraseQuery(it)) }
        }
}

fun searchAllTextForPhraseQuery(term: String): QueryBuilder {
    return MultiMatchQueryBuilder(term)
        .fields(exactInnerFieldsToWeight)
        .type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun searchUrlQuery(term: String): QueryBuilder {
    return MatchQueryBuilder(HREF, term).fuzziness(Fuzziness.AUTO)
}

private fun containsAllWordsInTermQuery(term: String) = MultiMatchQueryBuilder(term)
    .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
    .operator(Operator.AND)
    .apply { allTextFields.forEach { this.field(it) } }

private fun standardSearchQuery(term: String) = MultiMatchQueryBuilder(term)
    .fields(fieldsToWeight)
    .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))

private fun ngramSearchQuery(term: String): MultiMatchQueryBuilder? =
    MultiMatchQueryBuilder(term)
        .fields(ngramsInnerFieldsToWeight)
        .operator(Operator.AND)

private fun exactPhraseSearchQuery(term: String) = searchAllTextForPhraseQuery(term).boost(EXACT_PHRASE_MATCH_BOOST)
