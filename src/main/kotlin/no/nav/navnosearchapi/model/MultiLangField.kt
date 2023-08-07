package no.nav.navnosearchapi.model

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class MultiLangField(
    @Field(type = FieldType.Text) val english: String? = null,
    @Field(type = FieldType.Text) val norwegian: String? = null,
)