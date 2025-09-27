package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShelfResponse(
    val id: String,
    val title: String,
    val dukanId: String
)
