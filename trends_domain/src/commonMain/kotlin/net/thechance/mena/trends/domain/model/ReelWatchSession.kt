package net.thechance.mena.trends.domain.model

import kotlinx.datetime.LocalDateTime

data class ReelWatchSession(
    val reelId: String,
    val watchStartTime: LocalDateTime?,
    val watchEndTime: LocalDateTime?,
    val videoDurationInMilliseconds: Long,
    val percentageOfVideoWatched: Float,
)