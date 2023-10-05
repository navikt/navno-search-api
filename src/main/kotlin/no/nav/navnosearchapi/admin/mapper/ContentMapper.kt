package no.nav.navnosearchapi.admin.mapper

import no.nav.navnosearchapi.common.dto.ContentDto
import no.nav.navnosearchapi.common.enums.ValidMetatags
import no.nav.navnosearchapi.common.model.ContentDao
import no.nav.navnosearchapi.common.model.MultiLangField
import no.nav.navnosearchapi.common.utils.ENGLISH
import no.nav.navnosearchapi.common.utils.NORWEGIAN
import no.nav.navnosearchapi.common.utils.NORWEGIAN_BOKMAAL
import no.nav.navnosearchapi.common.utils.createInternalId
import no.nav.navnosearchapi.common.utils.norwegianLanguageCodes
import no.nav.navnosearchapi.common.utils.supportedLanguages
import org.springframework.data.elasticsearch.core.suggest.Completion
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
class ContentMapper {
    fun toContentDao(content: ContentDto, teamName: String): ContentDao {
        return ContentDao(
            id = createInternalId(teamName, content.id),
            teamOwnedBy = teamName,
            href = content.href,
            autocomplete = Completion(listOf(content.title)),
            title = toMultiLangField(content.title, content.metadata.language),
            ingress = toMultiLangField(content.ingress, content.metadata.language),
            text = toMultiLangField(content.text, content.metadata.language),
            createdAt = content.metadata.createdAt.atZone(ZoneId.systemDefault()),
            lastUpdated = content.metadata.lastUpdated.atZone(ZoneId.systemDefault()),
            audience = content.metadata.audience,
            language = resolveLanguage(content.metadata.language),
            isFile = content.metadata.isFile,
            fylke = content.metadata.fylke,
            metatags = resolveMetatags(content.metadata.metatags, content.metadata.fylke, content.metadata.isFile),
            keywords = content.metadata.keywords,
        )
    }

    fun resolveMetatags(metatags: List<String>, fylke: String?, isFile: Boolean): List<String> {
        if (metatags.isEmpty() && fylke == null && !isFile) {
            return listOf(ValidMetatags.INFORMASJON.descriptor)
        }
        return metatags
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