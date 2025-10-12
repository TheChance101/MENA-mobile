package net.thechance.mena.trends.presentation.screen.show_real

import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue

fun Reel.toUiState(): ReelUiState {
    return ReelUiState(
        id = id,
        profileImageUrl = "",//TODO
        userName = "",//TODO
        timeAgo = createdAt?.timeAgoValue(),
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        likes = likesCount,
        views = viewsCount
    )
}