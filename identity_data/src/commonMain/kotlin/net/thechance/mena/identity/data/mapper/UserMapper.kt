package net.thechance.mena.identity.data.mapper

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.ProfileResponseDto
import net.thechance.mena.identity.data.utils.formatAsString
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User


fun ProfileResponseDto.toDomain(): User {
    return User(
        firstName = this.firstName,
        lastName = this.lastName,
        profileImageUrl = this.imageUrl,
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
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username.lowercase(),
        profileImageUrl = this.imageUrl,
        birthDate = this.birthDate,
        gender = this.gender,
    )
}

fun UserEntity.toDomain(): User {
    return User(
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

fun User.toEntity(): UserEntity {
    return UserEntity(
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