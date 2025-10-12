package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DukanResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String
)