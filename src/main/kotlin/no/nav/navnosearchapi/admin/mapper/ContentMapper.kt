package no.nav.navnosearchapi.admin.mapper

import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.model.MultiLangField
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.NORWEGIAN
import no.nav.navnosearchapi.common.utils.NORWEGIAN_BOKMAAL
import no.nav.navnosearchapi.common.utils.createInternalId
import no.nav.navnosearchapi.common.utils.norwegianLanguageCodes
import no.nav.navnosearchapi.common.utils.supportedLanguages
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
class ContentMapper {
    fun toContentDao(content: ContentDto, teamName: String): ContentDao {
        return ContentDao(
            id = createInternalId(teamName, content.id),
            teamOwnedBy = teamName,
            href = content.href,
            autocomplete = content.title,
            title = toMultiLangField(content.title, content.metadata.language),
            ingress = toMultiLangField(content.ingress, content.metadata.language),
            text = toMultiLangField(content.text, content.metadata.language),
            createdAt = content.metadata.createdAt.atZone(ZoneId.systemDefault()),
            lastUpdated = content.metadata.lastUpdated.atZone(ZoneId.systemDefault()),
            audience = content.metadata.audience,
            language = resolveLanguage(content.metadata.language),
            isFile = content.metadata.isFile,
            fylke = content.metadata.fylke,
            metatags = content.metadata.metatags,
        )
    }

    fun resolveLanguage(language: String): String {
        if (language.equals(NORWEGIAN, ignoreCase = true)) {
            return NORWEGIAN_BOKMAAL
        }
        return language.lowercase()
    }

    fun toMultiLangField(value: String, language: String): MultiLangField {
        return MultiLangField(
            en = if (ENGLISH == language) value else null,
            no = if (norwegianLanguageCodes.contains(language)) value else null,
            other = if (!supportedLanguages.contains(language)) value else null,
        )
    }
}