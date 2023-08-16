package no.nav.navnosearchapi.service.search

fun searchAllTextQuery(term: String): String = """
    {
      "multi_match": {
        "query": "$term",
        "fields": ["name.*^3", "ingress.*^2", "text.*"],
        "fuzziness": "auto"
      }
    }
    """

fun searchAllTextForPhraseQuery(term: String): String = """
    {
      "multi_match": {
        "query": $term,
        "type": "phrase",
        "fields": ["name.*^3", "ingress.*^2", "text.*"]
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