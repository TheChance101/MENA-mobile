package net.thechance.mena.trends.presentation.screen.manage_my_trends

import net.thechance.mena.trends.domain.entity.Profile
import net.thechance.mena.trends.domain.entity.Reel


internal fun Reel.toUiState(): ReelUiState {
    return ReelUiState(
        id = id,
        thumbnailUrl = thumbnailUrl
    )
}


internal fun Profile.toUiState(): ProfileUiState {
    return ProfileUiState(
        userName = username,
        profileImageUrl = profileImageUrl
    )
}