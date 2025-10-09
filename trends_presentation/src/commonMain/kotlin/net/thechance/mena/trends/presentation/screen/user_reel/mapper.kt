package net.thechance.mena.trends.presentation.screen.user_reel

import net.thechance.mena.trends.domain.entity.Reel

fun Reel.toUserReelUiState() =
    UserReelUiState(
        id = id,
        videoUrl = videoUrl,
        description = description,
        likesCount = likesCount,
        viewsCount = viewsCount,
        createdAt = "",
        isCurrentUserOwner = isCurrentUserOwner
    )