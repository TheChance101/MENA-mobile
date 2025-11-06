package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.response.ProfileResponseDto
import net.thechance.mena.identity.data.utils.formatAsString
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
fun ProfileResponseDto.toDomain(): User {
    return User(
        id = Uuid.parse(this.id),
        firstName = this.firstName,
        lastName = this.lastName,
        profileImageUrl = normalizeUrl(this.imageUrl),
        username = this.username.lowercase(),
        birthDate = LocalDate.parse(this.birthDate),
        gender = when (this.gender) {
            UserEntity.MALE -> Gender.MALE
            else -> Gender.FEMALE
        }
    )
}

fun ProfileResponseDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username.lowercase(),
        profileImageUrl = normalizeUrl(this.imageUrl),
        birthDate = this.birthDate,
        gender = this.gender,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun UserEntity.toDomain(): User {
    return User(
        id = Uuid.parse(this.id),
        firstName = this.firstName,
        lastName = this.lastName,
        profileImageUrl = this.profileImageUrl,
        username = this.username.lowercase(),
        birthDate = LocalDate.parse(this.birthDate),
        gender = when (this.gender) {
            UserEntity.MALE -> Gender.MALE
            else -> Gender.FEMALE
        }
    )
}

@OptIn(ExperimentalUuidApi::class)
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id.toString(),
        firstName = this.firstName,
        lastName = this.lastName,
        profileImageUrl = this.profileImageUrl,
        username = this.username.lowercase(),
        birthDate = this.birthDate.formatAsString(),
        gender = when (this.gender) {
            Gender.MALE -> UserEntity.MALE
            else -> UserEntity.FEMALE
        }
    )
}

private fun normalizeUrl(url: String?): String {
    if (url == null)
        return ""
    val regex = Regex("^(https?://[^/]+)(/.*)?$")
    val match = regex.find(url)

    return if (match != null) {
        val domainPart = match.groupValues[1]
        val pathPart = match.groupValues.getOrNull(2)?.replace(Regex("/{2,}"), "/") ?: ""
        domainPart + pathPart
    } else {
        url
    }
}