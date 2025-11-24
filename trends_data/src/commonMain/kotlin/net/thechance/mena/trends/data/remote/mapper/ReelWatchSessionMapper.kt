package net.thechance.mena.trends.data.remote.mapper

import net.thechance.mena.trends.data.local.database.UserEngagement
import net.thechance.mena.trends.domain.model.TrendWatchSession

internal fun TrendWatchSession.toUserEngagement(userId: String): UserEngagement {
    return UserEngagement(
        userId = userId,
        trendId = trendId,
        watchStartTime = watchStartTime,
        watchEndTime = watchEndTime,
        videoDurationInMilliseconds = videoDurationInMilliseconds,
        percentageOfVideoWatched = percentageOfVideoWatched
    )
}