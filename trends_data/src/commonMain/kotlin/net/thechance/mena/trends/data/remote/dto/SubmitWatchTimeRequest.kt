package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitWatchTimeRequest(
	@SerialName("userId")
	val userId: String,
	@SerialName("watchTimes")
	val watchTimes: List<WatchTimeDto>
)

@Serializable
data class WatchTimeDto(
	@SerialName("trendId")
	val trendId: String,
	@SerialName("videoDuration")
	val videoDuration: Long,
	@SerialName("watchStartTimeStamp")
	val watchStartTimeStamp: String,
	@SerialName("watchEndTimeStamp")
	val watchEndTimeStamp: String,
	@SerialName("percentWatched")
	val percentWatched: Double
)
