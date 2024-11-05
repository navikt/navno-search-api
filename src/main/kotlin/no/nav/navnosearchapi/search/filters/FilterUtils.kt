package no.nav.navnosearchapi.search.filters

import no.nav.navnosearchadminapi.common.constants.AUDIENCE
import no.nav.navnosearchadminapi.common.constants.FYLKE
import no.nav.navnosearchadminapi.common.constants.METATAGS
import no.nav.navnosearchadminapi.common.constants.TYPE
import no.nav.navnosearchadminapi.common.enums.ValidAudiences
import no.nav.navnosearchadminapi.common.enums.ValidFylker
import no.nav.navnosearchadminapi.common.enums.ValidMetatags
import no.nav.navnosearchadminapi.common.enums.ValidTypes
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.ExistsQueryBuilder
import org.opensearch.index.query.QueryBuilder
import org.opensearch.index.query.TermQueryBuilder

fun BoolQueryBuilder.mustHaveMetatags(vararg metatags: ValidMetatags) = apply {
    metatags.forEach { must(TermQueryBuilder(METATAGS, it.descriptor)) }
}

fun BoolQueryBuilder.mustNotHaveMetatags(vararg metatags: ValidMetatags) = apply {
    metatags.forEach { mustNot(TermQueryBuilder(METATAGS, it.descriptor)) }
}

fun BoolQueryBuilder.mustHaveOneOfMetatags(vararg metatags: ValidMetatags) = apply {
    must(BoolQueryBuilder().shouldHaveMetatags(*metatags))
}

fun BoolQueryBuilder.shouldHaveMetatags(vararg metatags: ValidMetatags) = apply {
    metatags.forEach { should(TermQueryBuilder(METATAGS, it.descriptor)) }
}

fun BoolQueryBuilder.mustHaveTypes(vararg types: ValidTypes) = apply {
    types.forEach { must(TermQueryBuilder(TYPE, it.descriptor)) }
}

fun BoolQueryBuilder.mustNotHaveTypes(vararg types: ValidTypes) = apply {
    types.forEach { mustNot(TermQueryBuilder(TYPE, it.descriptor)) }
}

fun BoolQueryBuilder.mustHaveOneOfTypes(vararg types: ValidTypes) = apply {
    must(BoolQueryBuilder().shouldHaveTypes(*types))
}

fun BoolQueryBuilder.shouldHaveTypes(vararg types: ValidTypes) = apply {
    types.forEach { should(TermQueryBuilder(TYPE, it.descriptor)) }
}

fun BoolQueryBuilder.mustHaveFylker(vararg fylker: ValidFylker) = apply {
    fylker.forEach { must(TermQueryBuilder(FYLKE, it.descriptor)) }
}

fun BoolQueryBuilder.mustHaveField(fieldName: String) = apply {
    must(ExistsQueryBuilder(fieldName))
}

fun BoolQueryBuilder.mustNotHaveField(fieldName: String) = apply {
    mustNot(ExistsQueryBuilder(fieldName))
}

fun BoolQueryBuilder.mustHaveAudience(audience: ValidAudiences, isStrict: Boolean = false) = apply {
    must(BoolQueryBuilder()
        .should(TermQueryBuilder(AUDIENCE, audience.descriptor))
        .should(TermQueryBuilder(AUDIENCE, ValidAudiences.OTHER.descriptor))
        .apply {
            if (!isStrict) should(BoolQueryBuilder().mustNot(ExistsQueryBuilder(AUDIENCE)))
        })
}

fun Collection<BoolQueryBuilder>.joinToSingleQuery(
    operator: BoolQueryBuilder.(QueryBuilder) -> BoolQueryBuilder
): BoolQueryBuilder {
    val joinedQuery = BoolQueryBuilder()
    this.forEach { joinedQuery.operator(it) }
    return joinedQuery
}