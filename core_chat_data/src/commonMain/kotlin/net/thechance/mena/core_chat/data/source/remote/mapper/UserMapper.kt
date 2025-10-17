package net.thechance.mena.core_chat.data.source.remote.mapper

import net.thechance.mena.core_chat.data.source.remote.dto.UserDto
import net.thechance.mena.core_chat.domain.entity.User


fun UserDto.toDomain(): User {
    return User(
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        imageUrl = imageUrl
    )
}