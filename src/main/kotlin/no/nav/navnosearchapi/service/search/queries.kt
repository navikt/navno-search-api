package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.utils.INGRESS
import no.nav.navnosearchapi.utils.TEXT
import no.nav.navnosearchapi.utils.TITLE
import no.nav.navnosearchapi.utils.enumDescriptors
import no.nav.navnosearchapi.validation.ValidLanguages

private const val TITLE_WEIGHT = 3
private const val INGRESS_WEIGHT = 2
private const val TEXT_WEIGHT = 1

private val fieldsToWeightMap = mapOf(TITLE to TITLE_WEIGHT, INGRESS to INGRESS_WEIGHT, TEXT to TEXT_WEIGHT)

fun searchAllTextQuery(term: String): String = """
    {
      "multi_match": {
        "query": "$term",
        "fields": [${fields()}],
        "fuzziness": "auto"
      }
    }
    """

fun searchAllTextForPhraseQuery(term: String): String = """
    {
      "multi_match": {
        "query": $term,
        "type": "phrase",
        "fields": [${fields()}]
      }
    }
    """

fun searchAsYouTypeQuery(term: String): String = """
    {
      "match_phrase_prefix": {
        "title.searchAsYouType": "$term"
      }
    }
    """

fun filteredQuery(query: String, filters: String): String = """
    {
      "bool": {
        "must": $query,
        "filter": {
          "terms": $filters
        }
      }
    }
    """

private fun fields(): String {
    val fields = mutableListOf<String>()

    fieldsToWeightMap.forEach { (field, weight) ->
        for (language in enumDescriptors<ValidLanguages>()) {
            fields.add("\"${field}.$language^${weight}\"")
        }
    }

    return fields.joinToString(", ")
}