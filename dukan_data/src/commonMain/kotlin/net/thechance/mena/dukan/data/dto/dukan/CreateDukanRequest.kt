package net.thechance.mena.dukan.data.dto.dukan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class CreateDukanRequest(
    @SerialName("name")
    val name: String,
    @SerialName("categoryIds")
    val categoryIds: Set<Uuid>,
    @SerialName("address")
    val address: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("colorId")
    val colorId: Uuid,
    @SerialName("style")
    val style: String
)