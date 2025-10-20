package net.thechance.mena.trends.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("emoji")
    val emoji: String? = null,
    @SerialName("isSelected")
    val isSelected: Boolean? = null
)