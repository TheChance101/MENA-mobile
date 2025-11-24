package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateTrendRequestDTO(
    @SerialName("description")
    val description: String,
    @SerialName("categoryIds")
    val categoryIds: List<String>
)