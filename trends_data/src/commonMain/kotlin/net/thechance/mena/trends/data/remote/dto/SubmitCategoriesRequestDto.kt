package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SubmitCategoriesRequestDto(
    @SerialName("categoryIds")
    val categoryIds: List<String>? = null
)