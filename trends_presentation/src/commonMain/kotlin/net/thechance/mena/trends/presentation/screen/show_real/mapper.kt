package net.thechance.mena.trends.presentation.screen.show_real

import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue

fun Reel.toUiState(): ReelUiState {
    return ReelUiState(
        id = id,
        profileImageUrl = profileImageUrl,
        userName = userName,
        timeAgo = createdAt?.timeAgoValue(),
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        likesCount = likesCount,
        viewsCount = viewsCount,
        isLiked = isLiked
    )
}