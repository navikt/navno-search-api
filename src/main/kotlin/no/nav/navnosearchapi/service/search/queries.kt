package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.utils.VALID_LANGS

private const val NAME_WEIGHT = 3
private const val INGRESS_WEIGHT = 2
private const val TEXT_WEIGHT = 1

private val fieldsToWeightMap = mapOf("name" to NAME_WEIGHT, "ingress" to INGRESS_WEIGHT, "text" to TEXT_WEIGHT)

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
        "name.searchAsYouType": "$term"
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
        for (language in VALID_LANGS) {
            fields.add("\"${field}.$language^${weight}\"")
        }
    }

    return fields.joinToString(", ")
}