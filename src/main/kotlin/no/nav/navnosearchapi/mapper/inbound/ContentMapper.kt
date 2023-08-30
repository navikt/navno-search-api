package no.nav.navnosearchapi.mapper.inbound

import no.nav.navnosearchapi.dto.ContentDto
import no.nav.navnosearchapi.model.ContentDao
import no.nav.navnosearchapi.model.MultiLangField
import no.nav.navnosearchapi.utils.ENGLISH
import no.nav.navnosearchapi.utils.NORWEGIAN
import no.nav.navnosearchapi.utils.createInternalId
import org.springframework.stereotype.Component

@Component
class ContentMapper {
    fun toContentDao(content: ContentDto, teamName: String): ContentDao {
        return ContentDao(
            id = createInternalId(teamName, content.id),
            teamOwnedBy = teamName,
            href = content.href,
            title = toMultiLangField(content.title, content.language, searchAsYouType = true),
            ingress = toMultiLangField(content.ingress, content.language),
            text = toMultiLangField(content.text, content.language),
            audience = content.audience,
            language = content.language,
            isFile = content.isFile,
            fylke = content.fylke,
            metatags = content.metatags,
        )
    }

    fun toMultiLangField(value: String, language: String, searchAsYouType: Boolean = false): MultiLangField {
        return MultiLangField(
            en = if (ENGLISH == language) value else null,
            no = if (NORWEGIAN == language) value else null,
            searchAsYouType = if (searchAsYouType) value else null
        )
    }
}