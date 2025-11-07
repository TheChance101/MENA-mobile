package net.thechance.mena.faith.data.remote.model.mosque

import kotlinx.serialization.Serializable

@Serializable
data class MosqueDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val createdAt: String
)