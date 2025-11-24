package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendPathUrlsDto(
    @SerialName("videoPath") val videoPath: String? = null,
    @SerialName("thumbnailPath") val thumbnailPath: String? = null
)
