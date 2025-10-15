package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.addresses.AddressRequestDto
import net.thechance.mena.identity.domain.entity.Address

fun Address.toDto(): AddressRequestDto {
    return AddressRequestDto(
        id = id,
        latitude = latitude,
        longitude = longitude,
        addressType = addressType,
        addressLine = addressLine,
        otherAddressType = otherAddressType,
        isActive = isActive
    )
}