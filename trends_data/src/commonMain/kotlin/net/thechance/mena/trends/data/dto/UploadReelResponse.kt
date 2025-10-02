package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadReelResponse(
    @SerialName("reelId")
    val reelId: String? = null
)
