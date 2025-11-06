package net.thechance.mena.identity.domain.util

import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createAddress(
    id: Uuid? = null,
    latitude: Double = 0.toDouble(),
    longitude: Double = 0.toDouble(),
    addressLine: String = "",
    addressType: AddressType = AddressType.Home
): Address {
    return Address(
        id = id,
        latitude = latitude,
        longitude = longitude,
        addressLine = addressLine,
        addressType = addressType
    )
}