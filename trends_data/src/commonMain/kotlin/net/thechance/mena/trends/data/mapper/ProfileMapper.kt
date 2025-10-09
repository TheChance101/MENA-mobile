package net.thechance.mena.trends.data.mapper

import net.thechance.mena.trends.data.dto.UserInfoDto
import net.thechance.mena.trends.domain.entity.UserInfo

internal fun UserInfoDto.toEntity(): UserInfo {
    return UserInfo(
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty(),
        username = username.orEmpty()
    )
}