package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DukanCategoryResponse(
    @SerialName("categories")
    val categories: List<DukanCategoryDto>
)
@Serializable
data class DukanCategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String,
)