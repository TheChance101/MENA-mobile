package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateUserCategoriesResponse(
    @SerialName("updatedCategories")
    val updatedCategories: List<CategoryDto>? = null
)