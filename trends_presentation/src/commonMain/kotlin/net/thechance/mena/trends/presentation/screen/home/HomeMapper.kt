package net.thechance.mena.trends.presentation.screen.home

import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue

fun Trend.toUiState(): TrendUiState {
    return TrendUiState(
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