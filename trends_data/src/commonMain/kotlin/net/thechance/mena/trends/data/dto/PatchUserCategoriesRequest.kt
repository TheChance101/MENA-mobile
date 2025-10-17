package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PatchUserCategoriesRequest(
    @SerialName("add")
    val categoriesIdsToAdd: List<String>? = null,
    @SerialName("remove")
    val categoriesIdsToRemove: List<String>? = null
)