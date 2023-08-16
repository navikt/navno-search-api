package no.nav.navnosearchapi.mapper.inbound

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.NORWEGIAN
import org.springframework.stereotype.Component

@Component
class ContentMapper {
    fun toContentDao(content: ContentDto, teamName: String): ContentDao {
        return ContentDao(
            id = content.id,
            teamOwnedBy = teamName,
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