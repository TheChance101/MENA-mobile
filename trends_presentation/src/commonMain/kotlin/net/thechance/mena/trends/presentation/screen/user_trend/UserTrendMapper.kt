package net.thechance.mena.trends.presentation.screen.user_trend

import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.presentation.shared.util.timeAgoValue

internal fun Trend.toUserTrendUiState() = UserTrendUiState(
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

internal fun TrendWatchSessionState.toEntity(percentageOfVideoWatched: Float): TrendWatchSession {
    return TrendWatchSession(
        trendId = trendId,
        watchStartTime = watchStartTime,
        watchEndTime = watchEndTime,
        videoDurationInMilliseconds = videoDurationInMilliseconds,
        percentageOfVideoWatched = percentageOfVideoWatched
    )
}