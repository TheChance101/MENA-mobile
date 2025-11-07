package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class DukanDetailsDto(
    @SerialName("id")
    val id: Uuid,
    @SerialName("ownerId")
    val ownerId: Uuid,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("address")
    val address: String,
    @SerialName("color")
    val color: DukanColorDto,
    @SerialName("style")
    val style: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("isFavorite")
    val isFavorite: Boolean,
)