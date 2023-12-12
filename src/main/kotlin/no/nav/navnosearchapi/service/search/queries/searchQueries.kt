package no.nav.navnosearchapi.service.search.queries

import no.nav.navnosearchadminapi.common.constants.ALL_TEXT
import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.HREF
import no.nav.navnosearchadminapi.common.constants.INGRESS
import no.nav.navnosearchadminapi.common.constants.INGRESS_WILDCARD
import no.nav.navnosearchadminapi.common.constants.LANGUAGE
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_BOKMAAL
import no.nav.navnosearchadminapi.common.constants.NORWEGIAN_NYNORSK
import no.nav.navnosearchadminapi.common.constants.TEXT
import no.nav.navnosearchadminapi.common.constants.TEXT_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TITLE
import no.nav.navnosearchadminapi.common.constants.TITLE_WILDCARD
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.constants.languageSubfields
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import org.opensearch.common.lucene.search.function.FunctionScoreQuery
import org.opensearch.common.unit.Fuzziness
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.MatchQueryBuilder
import org.opensearch.index.query.MultiMatchQueryBuilder
import org.opensearch.index.query.Operator
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.TermQueryBuilder
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders

private const val TITLE_WEIGHT = 12.0f
private const val INGRESS_WEIGHT = 3.0f
private const val TEXT_WEIGHT = 1.0f
//private const val KEYWORDS_WEIGHT = 10.0f

private const val PRIVATPERSON_WEIGHT = 1.5f
private const val ARBEIDSGIVER_WEIGHT = 1.25f
private const val SAMARBEIDSPARTNER_WEIGHT = 1.0f

private const val NORWEGIAN_BOKMAAL_WEIGHT = 1.40f
private const val NORWEGIAN_NYNORSK_WEIGHT = 1.35f

private const val PRODUKTSIDE_WEIGHT = 8.0f
private const val TEMASIDE_WEIGHT = 6.0f
private const val SITUASJONSSIDE_WEIGHT = 4.0f
private const val GUIDE_WEIGHT = 2.0f

private const val FUZZY_LOW_DISTANCE = 5
private const val FUZZY_HIGH_DISTANCE = 8

private val WHITESPACE = "\\s+".toRegex()

const val EXACT_INNER_FIELD_PATH = ".exact"

// todo: Ta stilling til om keywords også bør være med her
private val fieldsToWeightMap = languageSubfields.flatMap {
    listOf(
        "$TITLE.$it" to TITLE_WEIGHT,
        "$INGRESS.$it" to INGRESS_WEIGHT,
        "$TEXT.$it" to TEXT_WEIGHT
    )
}.toMap()

private val allTextFields = languageSubfields.map { "$ALL_TEXT.$it" }

private val exactInnerFieldsToWeightMap = mapOf(
    TITLE_WILDCARD + EXACT_INNER_FIELD_PATH to TITLE_WEIGHT,
    INGRESS_WILDCARD + EXACT_INNER_FIELD_PATH to INGRESS_WEIGHT,
    TEXT_WILDCARD + EXACT_INNER_FIELD_PATH to TEXT_WEIGHT,
)

private val audienceToWeightMap = mapOf(
    ValidAudiences.PRIVATPERSON.descriptor to PRIVATPERSON_WEIGHT,
    ValidAudiences.ARBEIDSGIVER.descriptor to ARBEIDSGIVER_WEIGHT,
    ValidAudiences.SAMARBEIDSPARTNER.descriptor to SAMARBEIDSPARTNER_WEIGHT,
)

private val typeToWeightMap = mapOf(
    ValidTypes.PRODUKTSIDE.descriptor to PRODUKTSIDE_WEIGHT,
    ValidTypes.TEMASIDE.descriptor to TEMASIDE_WEIGHT,
    ValidTypes.SITUASJONSSIDE.descriptor to SITUASJONSSIDE_WEIGHT,
    ValidTypes.GUIDE.descriptor to GUIDE_WEIGHT,
)

private val languageToWeightMap = mapOf(
    NORWEGIAN_BOKMAAL to NORWEGIAN_BOKMAAL_WEIGHT,
    NORWEGIAN_NYNORSK to NORWEGIAN_NYNORSK_WEIGHT,
)

fun searchAllTextQuery(term: String): QueryBuilder {
    return BoolQueryBuilder()
        .should(
            MultiMatchQueryBuilder(term)
                .fields(fieldsToWeightMap)
                .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                .type(MultiMatchQueryBuilder.Type.MOST_FIELDS)
        ).apply {
            // Alle treff må inneholde alle søkeord
            if (term.split(WHITESPACE).size > 1) {
                this.filter(
                    MultiMatchQueryBuilder(term)
                        .fuzziness(Fuzziness.customAuto(FUZZY_LOW_DISTANCE, FUZZY_HIGH_DISTANCE))
                        .operator(Operator.AND)
                        .searchAllText()
                )
            }
        }
}

fun searchAllTextForPhraseQuery(term: String): QueryBuilder {
    return MultiMatchQueryBuilder(term)
        .fields(exactInnerFieldsToWeightMap)
        .type(MultiMatchQueryBuilder.Type.PHRASE)
}

fun searchUrlQuery(term: String): QueryBuilder {
    return MatchQueryBuilder(HREF, term).fuzziness(Fuzziness.AUTO)
}

fun QueryBuilder.applyWeighting(): FunctionScoreQueryBuilder {
    return this.applyAudienceWeighting().applyTypeWeighting().applyLanguageWeighting()
}

private fun QueryBuilder.applyAudienceWeighting(): FunctionScoreQueryBuilder {
    return multiplyScoreByFieldValue(this, AUDIENCE, audienceToWeightMap)
}

private fun QueryBuilder.applyTypeWeighting(): FunctionScoreQueryBuilder {
    return multiplyScoreByFieldValue(this, TYPE, typeToWeightMap)
}

private fun QueryBuilder.applyLanguageWeighting(): FunctionScoreQueryBuilder {
    return multiplyScoreByFieldValue(this, LANGUAGE, languageToWeightMap)
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