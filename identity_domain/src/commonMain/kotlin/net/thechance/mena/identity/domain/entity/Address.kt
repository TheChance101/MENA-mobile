package net.thechance.mena.identity.domain.entity

data class Address(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val otherAddressType: String?,
)
