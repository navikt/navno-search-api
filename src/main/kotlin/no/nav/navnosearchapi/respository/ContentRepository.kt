package no.nav.navnosearchapi.respository

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.utils.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.respository.utils.searchAllTextQuery
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ContentRepository : ElasticsearchRepository<Content, Long> {
    @Query(searchAllTextQuery)
    fun searchAllText(term: String): SearchHits<Content>

    @Query(searchAllTextForPhraseQuery)
    fun searchAllTextForPhrase(term: String): SearchHits<Content>
}