package no.nav.navnosearchapi.respository.utils

const val searchAllTextQuery = """
    {
      "multi_match": {
        "query": "?0",
        "fields": ["name^3", "ingress^2", "text"]
      }
    }
    """

const val searchAllTextForPhraseQuery = """
    {
      "multi_match": {
        "query": "?0",
        "type": "phrase",
        "fields": ["name^3", "ingress^2", "text"]
      }
    }
    """
