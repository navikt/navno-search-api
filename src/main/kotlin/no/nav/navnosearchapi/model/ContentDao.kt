package no.nav.navnosearchapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.CompletionField
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Dynamic
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting
import org.springframework.data.elasticsearch.annotations.WriteTypeHint
import org.springframework.data.elasticsearch.core.suggest.Completion
import java.time.ZonedDateTime


// Todo: Finne en annen måte å genere mappings og data i integrajonstester. Da kan mye kode fjernes.
@Document(
    indexName = "search-content-v7",
    dynamic = Dynamic.STRICT,
    /* Disabler type hints da det lager et _class-felt i mappingen som gir problemer for wildcard-søk.
       Bør skrives om dersom vi trenger polymorfisk data. */
    writeTypeHint = WriteTypeHint.FALSE,
)
@Setting(settingPath = "opensearch/index-settings.json")
data class ContentDao(
    @Id @Field(type = FieldType.Keyword) val id: String,
    @CompletionField val autocomplete: Completion,
    @Field(type = FieldType.Keyword) val teamOwnedBy: String,
    @Field(type = FieldType.Text) val href: String,
    @Field(type = FieldType.Object) val title: MultiLangField,
    @Field(type = FieldType.Object) val ingress: MultiLangField,
    @Field(type = FieldType.Object) val text: MultiLangField,
    @Field(type = FieldType.Date) val createdAt: ZonedDateTime,
    @Field(type = FieldType.Date) val lastUpdated: ZonedDateTime,
    @Field(type = FieldType.Keyword) val audience: List<String>,
    @Field(type = FieldType.Keyword) val language: String,
    @Field(type = FieldType.Boolean) val isFile: Boolean,
    @Field(type = FieldType.Keyword) val fylke: String? = null,
    @Field(type = FieldType.Keyword) val metatags: List<String>,
    @Field(type = FieldType.Text) val keywords: List<String> = emptyList(),
)