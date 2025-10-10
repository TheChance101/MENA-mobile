package net.thechance.mena.trends.presentation.screen.show_real

import net.thechance.mena.trends.domain.entity.Reel

fun Reel.toUiState(): TrendUiState {
    return TrendUiState(
        id = id,
        profileImageUrl = "",//TODO
        userName = "",//TODO
        timeAgo = createdAt,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        likes = likesCount,
        views = viewsCount
    )
}
