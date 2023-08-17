package no.nav.navnosearchapi.service.search

import no.nav.navnosearchapi.validation.ContentDtoValidator.Companion.VALID_LANGS

fun searchAllTextQuery(term: String): String = """
    {
      "multi_match": {
        "query": "$term",
        "fields": [${fields(mapOf("name" to 3, "ingress" to 2, "text" to 1))}],
        "fuzziness": "auto"
      }
    }
    """

fun searchAllTextForPhraseQuery(term: String): String = """
    {
      "multi_match": {
        "query": $term,
        "type": "phrase",
        "fields": [${fields(mapOf("name" to 3, "ingress" to 2, "text" to 1))}]
      }
    }
    """

fun searchAsYouTypeQuery(term: String): String = """
    {
      "multi_match": {
        "query": "$term",
        "type": "bool_prefix",
        "fields": [
          "name.searchAsYouType",
          "name.searchAsYouType._2gram",
          "name.searchAsYouType._3gram"
        ]
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

private fun fields(fieldToWeightMap: Map<String, Int>): String {
    val fields = mutableListOf<String>()

    fieldToWeightMap.forEach { (field, weight) ->
        for (language in VALID_LANGS) { // todo: flytt denne lista
            fields.add("\"${field}.$language^${weight}\"")
        }
    }

    return fields.joinToString(", ")
}