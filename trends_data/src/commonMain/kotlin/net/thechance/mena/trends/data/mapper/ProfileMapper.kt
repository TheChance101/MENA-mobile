package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.ProfileDto
import net.thechance.mena.trends.domain.entity.Profile

internal fun ProfileDto.toEntity(): Profile {
    return Profile(
        firstName = firstName,
        lastName = lastName,
        profileImageUrl = profileImageUrl.orEmpty(),
        username = username
    )
}