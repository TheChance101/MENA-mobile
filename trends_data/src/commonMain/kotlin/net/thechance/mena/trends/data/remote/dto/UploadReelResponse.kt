package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadReelResponse(
    @SerialName("trendId")
    val reelId: String? = null
)
