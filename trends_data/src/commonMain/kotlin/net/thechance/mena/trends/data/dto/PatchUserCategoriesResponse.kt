package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PatchUserCategoriesResponse(
    @SerialName("updatedCategories")
    val updatedCategories: List<CategoryDto>? = null
)