package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.ProfileDto
import net.thechance.mena.trends.domain.entity.Profile

internal fun ProfileDto.toEntity(): Profile {
    return Profile(
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty(),
        username = username.orEmpty()
    )
}