package net.thechance.mena.identity.data.dto.addresses

data class AddAddressRequestDto(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val otherAddressType: String? = null
)
