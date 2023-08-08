package no.nav.navnosearchapi.mapper.inbound

import no.nav.navnosearchapi.model.Content
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.NORWEGIAN
import org.springframework.stereotype.Component
import no.nav.navnosearchapi.dto.Content as ContentDto

@Component
class ContentMapper {
    fun toContentDao(content: ContentDto): Content {
        return Content(
            id = content.id,
            href = content.href,
            name = toMultiLangField(content.name, content.language),
            ingress = toMultiLangField(content.ingress, content.language),
            text = toMultiLangField(content.text, content.language),
            maalgruppe = content.maalgruppe,
            language = content.language,
        )
    }

    fun toMultiLangField(value: String, language: String): MultiLangField {
        return MultiLangField(
            en = if (ENGLISH == language) value else null,
            no = if (NORWEGIAN == language) value else null
        )
    }
}