package net.thechance.mena.identity.domain.model

import net.thechance.mena.identity.domain.entity.AddressType

data class AddressInput(
    val latitude: Double,
    val longitude: Double,
    val addressLine: String,
    val addressType: AddressType
)