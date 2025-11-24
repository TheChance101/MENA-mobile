package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadTrendResponse(
    @SerialName("trendId")
    val trendId: String? = null
)
