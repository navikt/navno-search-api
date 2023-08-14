package no.nav.navnosearchapi.model

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class MultiLangField(
    @Field(type = FieldType.Text, analyzer = "english") val en: String? = null,
    @Field(type = FieldType.Text, analyzer = "norwegian") val no: String? = null,
)