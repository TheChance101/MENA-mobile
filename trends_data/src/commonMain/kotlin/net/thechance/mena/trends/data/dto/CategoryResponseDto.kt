package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryResponseDto(
    @SerialName("data") val categories: List<CategoryDto>? = null
)
