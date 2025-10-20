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

@Serializable
data class DukanCategoryDto @OptIn(ExperimentalUuidApi::class) constructor(
    @SerialName("id")
    val id: Uuid,
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String,
)