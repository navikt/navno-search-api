package no.nav.navnosearchapi.model

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class MultiLangField(
    @Field(type = FieldType.Text) val en: String? = null,
    @Field(type = FieldType.Text) val no: String? = null,
)