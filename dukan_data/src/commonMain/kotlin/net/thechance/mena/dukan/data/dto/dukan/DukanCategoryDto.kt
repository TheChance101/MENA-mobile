package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable
data class DukanCategoryResponse(
    @SerialName("categories")
    val categories: List<DukanCategoryDto>
)

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class DukanCategoryDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String,
)