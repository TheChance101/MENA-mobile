package net.thechance.mena.trends.data.remote.mapper

import net.thechance.mena.trends.data.local.database.UserEngagement
import net.thechance.mena.trends.domain.model.ReelWatchSession

internal fun ReelWatchSession.toUserEngagement(userId: String): UserEngagement {
    return UserEngagement(
        userId = userId,
        trendId = reelId,
        watchStartTimestamp = watchStartTime,
        watchEndTimestamp = watchEndTime,
        videoDurationInMilliseconds = videoDurationInMilliseconds,
        percentageOfVideoWatched = percentageOfVideoWatched
    )
}