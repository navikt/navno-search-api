package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.ALL_TEXT
import no.nav.navnosearchadminapi.common.constants.EXACT_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.HREF
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.NGRAMS_INNER_FIELD
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import no.nav.navnosearchapi.service.search.SearchService.Companion.whitespace
import org.opensearch.common.lucene.search.function.FunctionScoreQuery
import org.opensearch.common.unit.Fuzziness
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.DisMaxQueryBuilder
import org.opensearch.index.query.MatchQueryBuilder
import org.opensearch.index.query.MultiMatchQueryBuilder
import org.opensearch.index.query.Operator
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders

private const val TITLE_WEIGHT = 12.0f
private const val INGRESS_WEIGHT = 3.0f
private const val TEXT_WEIGHT = 0.01f

private const val EXACT_TITLE_WEIGHT = 12.0f
private const val EXACT_INGRESS_WEIGHT = 6.0f
private const val EXACT_TEXT_WEIGHT = 1.0f

private const val NGRAM_TITLE_WEIGHT = 11.5f
private const val NGRAM_INGRESS_WEIGHT = 3.0f

private const val OVERSIKT_WEIGHT = 2.0f
private const val PRODUKTSIDE_WEIGHT = 2.0f
private const val TEMASIDE_WEIGHT = 1.75f
private const val GUIDE_WEIGHT = 1.75f

private const val SITUASJONSSIDE_WEIGHT = 1.50f
private const val EXACT_PHRASE_MATCH_BOOST = 1.5f

private const val FUZZY_LOW_DISTANCE = 6
private const val FUZZY_HIGH_DISTANCE = 8

private const val NGRAM_MIN_LENGTH = 4

private val allTextFields = languageSubfields.flatMap {
    listOf(
        "$ALL_TEXT.$it",
        "$TITLE.$it.$NGRAMS_INNER_FIELD",
        "$INGRESS.$it.$NGRAMS_INNER_FIELD",
    )
}.toList()

private val fieldsToWeightMap = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it" to TITLE_WEIGHT,
        "$INGRESS.$it" to INGRESS_WEIGHT,
        "$TEXT.$it" to TEXT_WEIGHT,
    )
}.toMap()

private val ngramsInnerFieldsToWeightMap = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it.$NGRAMS_INNER_FIELD" to NGRAM_TITLE_WEIGHT,
        "$INGRESS.$it.$NGRAMS_INNER_FIELD" to NGRAM_INGRESS_WEIGHT,
    )
}.toMap()

private val exactInnerFieldsToWeightMap = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it.$EXACT_INNER_FIELD" to EXACT_TITLE_WEIGHT,
        "$INGRESS.$it.$EXACT_INNER_FIELD" to EXACT_INGRESS_WEIGHT,
        "$TEXT.$it.$EXACT_INNER_FIELD" to EXACT_TEXT_WEIGHT,
    )
}.toMap()

private val typeToWeightMap = mapOf(
    ValidTypes.OVERSIKT.descriptor to OVERSIKT_WEIGHT,
    ValidTypes.PRODUKTSIDE.descriptor to PRODUKTSIDE_WEIGHT,
    ValidTypes.TEMASIDE.descriptor to TEMASIDE_WEIGHT,
    ValidTypes.SITUASJONSSIDE.descriptor to SITUASJONSSIDE_WEIGHT,
    ValidTypes.GUIDE.descriptor to GUIDE_WEIGHT,
)

fun searchAllTextQuery(term: String): QueryBuilder {
    return BoolQueryBuilder()
        // Filter (bidrar ikke til score) - alle treff må inneholde alle søkeord (på tvers av feltene)
        .filter(
            MultiMatchQueryBuilder(term)
                .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                .operator(Operator.AND)
                .searchAllText()
        )
        .should(
            // Bruk subquery med høyest score
            DisMaxQueryBuilder()
                // Standard søk i title, ingress og text
                .add(
                    MultiMatchQueryBuilder(term)
                        .fields(fieldsToWeightMap)
                        .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                )
                // Ngram-søk i title og ingress (kun dersom søketerm er minst 4 tegn)
                .addIfTermHasMinLength(
                    term,
                    NGRAM_MIN_LENGTH,
                    MultiMatchQueryBuilder(term)
                        .fields(ngramsInnerFieldsToWeightMap)
                        .operator(Operator.AND)
                )
                // Eksakt frase-søk (kun ved flere søkeord)
                .addIfMultipleWordsInTerm(
                    term,
                    searchAllTextForPhraseQuery(term)
                        .boost(EXACT_PHRASE_MATCH_BOOST)
                )
        )
}

fun DisMaxQueryBuilder.addIfMultipleWordsInTerm(term: String, query: QueryBuilder): DisMaxQueryBuilder {
    if (term.split(whitespace).size > 1) {
        this.add(query)
    }
    return this
}

fun DisMaxQueryBuilder.addIfTermHasMinLength(
    term: String,
    minLength: Int,
    query: QueryBuilder
): DisMaxQueryBuilder {
    if (term.trim().length >= minLength) {
        this.add(query)
    }
    return this
}

fun searchAllTextForPhraseQuery(term: String): QueryBuilder {
    return MultiMatchQueryBuilder(term)
        .fields(exactInnerFieldsToWeightMap)
        .type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun searchUrlQuery(term: String): QueryBuilder {
    return MatchQueryBuilder(HREF, term).fuzziness(Fuzziness.AUTO)
}

fun QueryBuilder.applyFilters(filterQuery: BoolQueryBuilder?): QueryBuilder {
    return filterQuery?.let { BoolQueryBuilder().must(this).filter(filterQuery) } ?: this
}

fun QueryBuilder.applyTypeWeighting(): FunctionScoreQueryBuilder {
    return multiplyScoreByFieldValue(this, TYPE, typeToWeightMap)
}

private fun MultiMatchQueryBuilder.searchAllText(): MultiMatchQueryBuilder {
    return this.apply { allTextFields.forEach { this.field(it) } }
}

private fun multiplyScoreByFieldValue(
    baseQuery: QueryBuilder,
    fieldName: String,
    valueToWeightMap: Map<String, Float>
): FunctionScoreQueryBuilder {
    return FunctionScoreQueryBuilder(
        baseQuery,
        valueToWeightMap.map {
            FunctionScoreQueryBuilder.FilterFunctionBuilder(
                TermQueryBuilder(fieldName, it.key),
                ScoreFunctionBuilders.weightFactorFunction(it.value)
            )
        }.toTypedArray()
    ).scoreMode(FunctionScoreQuery.ScoreMode.MAX)
}