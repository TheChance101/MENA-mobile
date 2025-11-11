package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class DukanSearchDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("isFavorite")
    val isFavorite: Boolean,
)

