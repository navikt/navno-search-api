package no.nav.navnosearchapi.respository

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.respository.utils.findAllByIndexQuery
import no.nav.navnosearchapi.respository.utils.searchAllTextForPhraseQuery
import no.nav.navnosearchapi.respository.utils.searchAllTextQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ContentRepository : ElasticsearchRepository<Content, String> {
    @Query(searchAllTextQuery)
    fun searchAllText(term: String, pageable: Pageable? = null): Page<Content>

    @Query(searchAllTextForPhraseQuery)
    fun searchAllTextForPhrase(term: String, pageable: Pageable? = null): Page<Content>

    @Query(findAllByIndexQuery)
    fun findAllByIndex(appName: String, pageable: Pageable? = null): Page<Content>
}