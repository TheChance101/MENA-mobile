package net.thechance.mena.identity.data.dto.addresses

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
    @SerialName("otherAddressTyped")
    val otherAddressType: String?,
    @SerialName("isActive")
    val isActive: Boolean
)
