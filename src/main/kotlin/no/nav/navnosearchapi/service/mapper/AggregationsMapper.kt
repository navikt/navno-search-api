package no.nav.navnosearchapi.service.mapper

import no.nav.navnosearchapi.rest.Params
import no.nav.navnosearchapi.service.dto.Aggregations
import no.nav.navnosearchapi.service.dto.Bucket
import no.nav.navnosearchapi.service.dto.FacetBucket
import no.nav.navnosearchapi.service.dto.UnderAggregations
import no.nav.navnosearchapi.service.utils.AggregationNames
import no.nav.navnosearchapi.service.utils.FacetKeys
import no.nav.navnosearchapi.service.utils.FacetNames
import no.nav.navnosearchapi.service.utils.UnderFacetKeys
import no.nav.navnosearchapi.service.utils.UnderFacetNames
import org.springframework.stereotype.Component

@Component
class AggregationsMapper {
    fun toAggregations(aggregations: Map<String, Long>, params: Params): Aggregations {
        fun facetAggregation(key: String, name: String, underFacetAggregations: List<FacetBucket>? = null) =
            FacetBucket(
                key = key,
                name = name,
                docCount = aggregations[name] ?: 0,
                checked = key == params.f,
                underaggregeringer = underFacetAggregations?.let { UnderAggregations(it) } ?: UnderAggregations()
            )

        fun underFacetAggregation(key: String, name: String, aggregationName: String = name) = FacetBucket(
            key = key,
            name = name,
            docCount = aggregations[aggregationName] ?: 0,
            checked = key in params.uf,
        )

        return Aggregations(
            fasetter = UnderAggregations(
                buckets = listOf(
                    facetAggregation(
                        key = FacetKeys.PRIVATPERSON,
                        name = FacetNames.PRIVATPERSON,
                        underFacetAggregations = privatpersonUnderFacets(::underFacetAggregation)
                    ),
                    facetAggregation(
                        key = FacetKeys.ARBEIDSGIVER,
                        name = FacetNames.ARBEIDSGIVER,
                        underFacetAggregations = arbeidsgiverUnderFacets(::underFacetAggregation)
                    ),
                    facetAggregation(
                        key = FacetKeys.SAMARBEIDSPARTNER,
                        name = FacetNames.SAMARBEIDSPARTNER,
                        underFacetAggregations = samarbeidspartnerUnderFacets(::underFacetAggregation)
                    ),
                    facetAggregation(
                        key = FacetKeys.PRESSE,
                        name = FacetNames.PRESSE,
                    ),
                    facetAggregation(
                        key = FacetKeys.STATISTIKK,
                        name = FacetNames.STATISTIKK,
                        underFacetAggregations = statistikkUnderFacets(::underFacetAggregation),
                    ),
                    facetAggregation(
                        key = FacetKeys.ANALYSER_OG_FORSKNING,
                        name = FacetNames.ANALYSER_OG_FORSKNING,
                        underFacetAggregations = analyseUnderFacets(::underFacetAggregation),
                    ),
                    facetAggregation(
                        key = FacetKeys.INNHOLD_FRA_FYLKER,
                        name = FacetNames.INNHOLD_FRA_FYLKER,
                        underFacetAggregations = fylkeUnderFacets(::underFacetAggregation),
                    ),
                )
            )
        )
    }

    private fun privatpersonUnderFacets(underFacetAggregation: (key: String, name: String, aggregationName: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(
                UnderFacetKeys.INFORMASJON,
                UnderFacetNames.INFORMASJON,
                AggregationNames.PRIVATPERSON_INFORMASJON
            ),
            underFacetAggregation(
                UnderFacetKeys.KONTOR,
                UnderFacetNames.KONTOR,
                AggregationNames.PRIVATPERSON_KONTOR
            ),
            underFacetAggregation(
                UnderFacetKeys.SOKNAD_OG_SKJEMA,
                UnderFacetNames.SOKNAD_OG_SKJEMA,
                AggregationNames.PRIVATPERSON_SOKNAD_OG_SKJEMA
            ),
            underFacetAggregation(
                UnderFacetKeys.AKTUELT,
                UnderFacetNames.AKTUELT,
                AggregationNames.PRIVATPERSON_AKTUELT
            ),
        )
    }

    private fun arbeidsgiverUnderFacets(underFacetAggregation: (key: String, name: String, aggregationName: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(
                UnderFacetKeys.INFORMASJON,
                UnderFacetNames.INFORMASJON,
                AggregationNames.ARBEIDSGIVER_INFORMASJON
            ),
            underFacetAggregation(
                UnderFacetKeys.KONTOR,
                UnderFacetNames.KONTOR,
                AggregationNames.ARBEIDSGIVER_KONTOR
            ),
            underFacetAggregation(
                UnderFacetKeys.SOKNAD_OG_SKJEMA,
                UnderFacetNames.SOKNAD_OG_SKJEMA,
                AggregationNames.ARBEIDSGIVER_SOKNAD_OG_SKJEMA
            ),
            underFacetAggregation(
                UnderFacetKeys.AKTUELT,
                UnderFacetNames.AKTUELT,
                AggregationNames.ARBEIDSGIVER_AKTUELT
            ),
        )
    }

    private fun samarbeidspartnerUnderFacets(underFacetAggregation: (key: String, name: String, aggregationName: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(
                UnderFacetKeys.INFORMASJON,
                UnderFacetNames.INFORMASJON,
                AggregationNames.SAMARBEIDSPARTNER_INFORMASJON
            ),
            underFacetAggregation(
                UnderFacetKeys.KONTOR,
                UnderFacetNames.KONTOR,
                AggregationNames.SAMARBEIDSPARTNER_KONTOR
            ),
            underFacetAggregation(
                UnderFacetKeys.SOKNAD_OG_SKJEMA,
                UnderFacetNames.SOKNAD_OG_SKJEMA,
                AggregationNames.SAMARBEIDSPARTNER_SOKNAD_OG_SKJEMA
            ),
            underFacetAggregation(
                UnderFacetKeys.AKTUELT,
                UnderFacetNames.AKTUELT,
                AggregationNames.SAMARBEIDSPARTNER_AKTUELT
            ),
        )
    }

    private fun statistikkUnderFacets(underFacetAggregation: (key: String, name: String, aggregationName: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(
                UnderFacetKeys.ARTIKLER,
                UnderFacetNames.ARTIKLER,
                AggregationNames.STATISTIKK_ARTIKLER
            ),
            underFacetAggregation(
                UnderFacetKeys.NYHETER,
                UnderFacetNames.NYHETER,
                AggregationNames.STATISTIKK_NYHETER
            ),
            underFacetAggregation(
                UnderFacetKeys.TABELLER,
                UnderFacetNames.TABELLER,
                AggregationNames.STATISTIKK_TABELLER
            ),
        )
    }

    private fun analyseUnderFacets(underFacetAggregation: (key: String, name: String, aggregationName: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(
                UnderFacetKeys.ARTIKLER,
                UnderFacetNames.ARTIKLER,
                AggregationNames.ANALYSER_OG_FORSKNING_ARTIKLER
            ),
            underFacetAggregation(
                UnderFacetKeys.NYHETER,
                UnderFacetNames.NYHETER,
                AggregationNames.ANALYSER_OG_FORSKNING_NYHETER
            ),
        )
    }

    private fun fylkeUnderFacets(underFacetAggregation: (key: String, name: String) -> FacetBucket): List<FacetBucket> {
        return filteredBuckets(
            underFacetAggregation(UnderFacetKeys.AGDER, UnderFacetNames.AGDER),
            underFacetAggregation(UnderFacetKeys.INNLANDET, UnderFacetNames.INNLANDET),
            underFacetAggregation(UnderFacetKeys.MORE_OG_ROMSDAL, UnderFacetNames.MORE_OG_ROMSDAL),
            underFacetAggregation(UnderFacetKeys.NORDLAND, UnderFacetNames.NORDLAND),
            underFacetAggregation(UnderFacetKeys.OSLO, UnderFacetNames.OSLO),
            underFacetAggregation(UnderFacetKeys.ROGALAND, UnderFacetNames.ROGALAND),
            underFacetAggregation(UnderFacetKeys.TROMS_OG_FINNMARK, UnderFacetNames.TROMS_OG_FINNMARK),
            underFacetAggregation(UnderFacetKeys.TRONDELAG, UnderFacetNames.TRONDELAG),
            underFacetAggregation(UnderFacetKeys.VESTFOLD_OG_TELEMARK, UnderFacetNames.VESTFOLD_OG_TELEMARK),
            underFacetAggregation(UnderFacetKeys.VESTLAND, UnderFacetNames.VESTLAND),
            underFacetAggregation(UnderFacetKeys.VEST_VIKEN, UnderFacetNames.VEST_VIKEN),
            underFacetAggregation(UnderFacetKeys.OST_VIKEN, UnderFacetNames.OST_VIKEN),
        )
    }

    private fun <T : Bucket> filteredBuckets(vararg buckets: T): List<T> {
        return buckets.filter { b -> b.docCount > 0 }
    }
}