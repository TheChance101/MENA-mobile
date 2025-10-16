package net.thechance.mena.trends.presentation.screen.user_reel

import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue

internal fun Reel.toUserReelUiState() =
    UserReelUiState(
        id = id,
        videoUrl = videoUrl,
        description = description,
        likesCount = likesCount,
        viewsCount = viewsCount,
        createdAt = createdAt?.timeAgoValue(),
        isCurrentUserOwner = isCurrentUserOwner,
        username = userName,
        profileImageUrl = profileImageUrl,
        isLiked = isLiked
    )