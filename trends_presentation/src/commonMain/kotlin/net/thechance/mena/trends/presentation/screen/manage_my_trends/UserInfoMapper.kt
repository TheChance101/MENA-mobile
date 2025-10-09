package net.thechance.mena.trends.presentation.screen.manage_my_trends

import net.thechance.mena.trends.domain.entity.UserInfo

internal fun UserInfo.toUiState(): UserInfoUiState {
    return UserInfoUiState(
        userName = username,
        profileImageUrl = profileImageUrl
    )
}