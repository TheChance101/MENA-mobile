package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
@Serializable
data class ShelfDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val name: String
)