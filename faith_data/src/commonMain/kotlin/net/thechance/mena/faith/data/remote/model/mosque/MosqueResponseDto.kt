package net.thechance.mena.faith.data.remote.model.mosque

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class MosqueResponseDto @OptIn(ExperimentalUuidApi::class) constructor(
    @SerialName("id")
    val id: Uuid,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("imageUrl")
    val image: String,
)
