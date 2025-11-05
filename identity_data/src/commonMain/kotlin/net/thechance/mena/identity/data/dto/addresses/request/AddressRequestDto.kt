package net.thechance.mena.identity.data.dto.addresses.request

import kotlinx.serialization.Serializable

@Serializable
data class AddressRequestDto(
    val id: String? = null,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val isActive: Boolean
)