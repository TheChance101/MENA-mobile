package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShelfDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String
)
