package no.nav.navnosearchapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "content")
data class Content(
    @Id val id: String,
    @Field val href: String? = null,
    @Field val name: String? = null,
    @Field val ingress: String? = null,
    @Field val text: String? = null,
)