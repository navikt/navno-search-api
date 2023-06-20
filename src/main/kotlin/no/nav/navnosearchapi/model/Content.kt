package no.nav.navnosearchapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "content")
data class Content(
    @Id val id: String,
    @Field val href: String,
    @Field val name: String,
    @Field val ingress: String,
    @Field val text: String,
)