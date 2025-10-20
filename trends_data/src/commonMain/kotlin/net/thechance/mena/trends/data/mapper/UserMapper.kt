package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.UserDto
import net.thechance.mena.trends.domain.entity.User

internal fun UserDto.toEntity(): User {
    return User(
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty(),
        username = username.orEmpty()
    )
}