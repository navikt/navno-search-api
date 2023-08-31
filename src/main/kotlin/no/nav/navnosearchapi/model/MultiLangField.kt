package no.nav.navnosearchapi.model

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class MultiLangField(
    @Field(type = FieldType.Text, analyzer = "english_with_html_strip") val en: String? = null,
    @Field(type = FieldType.Text, analyzer = "norwegian_with_html_strip") val no: String? = null,
    @Field(type = FieldType.Text, analyzer = "standard_with_html_strip") val other: String? = null,
    @Field(type = FieldType.Search_As_You_Type) val searchAsYouType: String? = null,
)