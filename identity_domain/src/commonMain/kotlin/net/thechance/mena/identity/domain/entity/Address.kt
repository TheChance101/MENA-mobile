package net.thechance.mena.identity.domain.entity

data class Address(
    val id:String? = null,
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: String,
    val otherAddressType: String?,
    val isActive:Boolean
)
