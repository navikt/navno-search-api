package no.nav.navnosearchapi.golden

import no.nav.navnosearchadminapi.common.model.Content
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations

/**
 * Lager indeksen og testdataene som golden-testene i test/http/ tas opp mot (se test/http/README.md). Indeksen lages
 * fra Content-klassen i felles-biblioteket, slik navno-search-admin-api lager den i prod, så mapping, analyzere og
 * datoformater blir de samme.
 *
 * Kjører bare når GOLDEN_OPENSEARCH_URI er satt, fra .github/workflows/record-goldens.yml. Den sletter indeksen
 * først, så den må aldri pekes mot en ekte OpenSearch.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "opensearch.uris=\${GOLDEN_OPENSEARCH_URI}",
        "opensearch.username=golden",
        "opensearch.password=golden-password",
    ],
)
@EnabledIfEnvironmentVariable(named = "GOLDEN_OPENSEARCH_URI", matches = "http://(localhost|127\\.0\\.0\\.1):\\d+")
class GoldenFixtures {
    @Autowired
    lateinit var operations: ElasticsearchOperations

    @Test
    fun `lager indeksen og legger inn testdataene`() {
        val indexOps = operations.indexOps(Content::class.java)
        if (indexOps.exists()) indexOps.delete()
        indexOps.createWithMapping()
        operations.save(goldenDocuments)
        indexOps.refresh()
    }
}
