package net.thechance.mena.trends.presentation.screen.manage_my_trends

import net.thechance.mena.identity.domain.entity.User

internal fun User.toUiState(): UserInfoUiState {
    return UserInfoUiState(
        userName = username,
        profileImageUrl = profileImageUrl
    )
}