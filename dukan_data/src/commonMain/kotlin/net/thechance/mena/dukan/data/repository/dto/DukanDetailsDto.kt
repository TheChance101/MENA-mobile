package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanDetailsDto(
    @SerialName("id")
    val id: String,
    @SerialName("ownerId")
    val ownerId: String,
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
    val longitude: Double

)

