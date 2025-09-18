package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDukanRequest(
    @SerialName("name")
    val name: String,
    @SerialName("categoryIds")
    val categoryIds: List<String>,
    @SerialName("address")
    val address: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("colorId")
    val colorId: String,
    @SerialName("style")
    val style: String
)