package net.thechance.mena.identity.data.dto.addresses.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("addressLine")
    val addressLine: String,
    @SerialName("addressType")
    val addressType: String,
    @SerialName("isActive")
    val isActive: Boolean
)