package net.thechance.mena.trends.data.remote.mapper

import net.thechance.mena.trends.data.local.database.UserEngagement
import net.thechance.mena.trends.data.remote.dto.WatchTimeDto

internal fun UserEngagement.toDto(): WatchTimeDto {
    return WatchTimeDto(
        trendId = trendId,
        videoDuration = videoDurationInMilliseconds,
        watchStartTimeStamp = watchStartTime.toString(),
        watchEndTimeStamp = watchEndTime.toString(),
        percentWatched = percentageOfVideoWatched.toDouble()
    )
}