package no.nav.navnosearchapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Dynamic
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.WriteTypeHint

@Document(
    indexName = "search-content",
    createIndex = false,
    dynamic = Dynamic.STRICT,
    /* Disabler type hints da det lager et _class-felt i mappingen som gir problemer for wildcard-søk.
       Bør skrives om dersom vi trenger polymorfisk data. */
    writeTypeHint = WriteTypeHint.FALSE,
)
data class ContentDao(
    @Id @Field(type = FieldType.Keyword) val id: String,
    @Field(type = FieldType.Text) val href: String,
    @Field(type = FieldType.Object) val name: MultiLangField,
    @Field(type = FieldType.Object) val ingress: MultiLangField,
    @Field(type = FieldType.Object) val text: MultiLangField,
    @Field(type = FieldType.Keyword) val maalgruppe: String,
    @Field(type = FieldType.Keyword) val language: String,
)