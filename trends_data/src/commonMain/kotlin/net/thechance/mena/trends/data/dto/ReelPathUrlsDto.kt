package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReelPathUrlsDto(
    @SerialName("videoPath") val videoPath: String,
    @SerialName("thumbnailPath") val thumbnailPath: String
)
