package net.thechance.mena.trends.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateUserCategoriesRequest(
    @SerialName("add")
    val categoriesIdsToAdd: List<String>? = null,
    @SerialName("remove")
    val categoriesIdsToRemove: List<String>? = null
)