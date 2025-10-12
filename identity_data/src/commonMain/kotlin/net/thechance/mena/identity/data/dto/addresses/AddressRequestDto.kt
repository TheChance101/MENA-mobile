package net.thechance.mena.identity.data.dto.addresses

import kotlinx.serialization.Serializable

@Serializable
data class AddressRequestDto(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val otherAddressType: String?,
)
