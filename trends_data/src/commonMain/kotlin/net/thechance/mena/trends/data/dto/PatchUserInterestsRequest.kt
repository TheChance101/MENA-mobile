package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PatchUserInterestsRequest(
    @SerialName("add")
    val interestsIdsToAdd: List<String>? = null,
    @SerialName("remove")
    val interestsIdsToRemove: List<String>? = null
)