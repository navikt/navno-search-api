package no.nav.navnosearchapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "search_content", createIndex = false)
data class Content(
    @Id val id: String,
    @Field(type = FieldType.Text) val href: String? = null,
    @Field(type = FieldType.Text) val name: String? = null,
    @Field(type = FieldType.Text) val ingress: String? = null,
    @Field(type = FieldType.Text) val text: String? = null,
    @Field(type = FieldType.Keyword) val maalgruppe: String? = null,
)