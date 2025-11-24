package net.thechance.mena.trends.domain.model

import kotlinx.datetime.LocalDateTime

data class TrendWatchSession(
    val trendId: String,
    val watchStartTime: LocalDateTime?,
    val watchEndTime: LocalDateTime?,
    val videoDurationInMilliseconds: Long,
    val percentageOfVideoWatched: Float,
)