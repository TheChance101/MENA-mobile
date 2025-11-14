package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("activationStatus")
    val activationStatus: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("categories")
    val categories: List<CategoryDto>? = null
)