package no.nav.navnosearchapi.config

import jakarta.annotation.PostConstruct
import no.nav.navnosearchapi.model.ContentDao
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.IndexOperations
import org.springframework.data.elasticsearch.core.index.AliasAction
import org.springframework.data.elasticsearch.core.index.AliasActionParameters
import org.springframework.data.elasticsearch.core.index.AliasActions
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest
import org.springframework.stereotype.Component

@Component
class TemplateInitializer(val operations: ElasticsearchOperations) {

    @PostConstruct
    fun setup() {
        val indexOps = operations.indexOps(ContentDao::class.java)

        if (!indexOps.existsTemplate(TEMPLATE_NAME)) {
            val mapping = indexOps.createMapping()

            val request = PutTemplateRequest.builder(TEMPLATE_NAME, TEMPLATE_PATTERN)
                .withMappings(mapping)
                .withAliasActions(aliasActions(indexOps))
                .withSettings(mapOf(COERCE_SETTING to false))
                .build()


            // Todo: Bruk nytt _index_template api når det støttes av rammeverket
            // https://github.com/opensearch-project/spring-data-opensearch/issues/149
            indexOps.putTemplate(request)
        }
    }

    private fun aliasActions(indexOps: IndexOperations) = AliasActions(
        AliasAction.Add(
            AliasActionParameters.builderForTemplate()
                .withAliases(*indexOps.indexCoordinates.indexNames)
                .build()
        )
    )

    companion object {
        private const val TEMPLATE_NAME = "search-content-template"
        private const val TEMPLATE_PATTERN = "search-content_*"

        private const val COERCE_SETTING = "index.mapping.coerce"
    }
}