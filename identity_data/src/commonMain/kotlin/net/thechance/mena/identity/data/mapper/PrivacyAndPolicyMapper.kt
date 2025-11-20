package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.PrivacyAndPolicyResponseDto
import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.SectionResponseDto
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy
import net.thechance.mena.identity.domain.model.Section
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun PrivacyAndPolicyResponseDto.toDomain(): PrivacyAndPolicy{
    return PrivacyAndPolicy(
        updateDate = parseLocalDateOrDefault(this.updateDate),
        sections = this.sections?.map { it.toDomain() } ?: emptyList()
    )
}

fun SectionResponseDto.toDomain(): Section{
    return Section(
        title = this.title,
        content = this.content
    )
}

@OptIn(ExperimentalTime::class)
fun parseLocalDateOrDefault(value: String?, defaultDate: LocalDate = DEFAULT_DATE): LocalDate {
    return value?.let {
        runCatching {
            val normalized = if (it.endsWith("Z") || it.contains('+')) it else it + "Z"
            val instant = Instant.parse(normalized)
            val userZone = TimeZone.currentSystemDefault()
            instant.toLocalDateTime(userZone).date
        }.getOrDefault(defaultDate)
    } ?: defaultDate
}

private val DEFAULT_DATE = LocalDate(2025, 12, 1)