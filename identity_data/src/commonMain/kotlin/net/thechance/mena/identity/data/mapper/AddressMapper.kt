package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.addresses.AddressRequestDto
import net.thechance.mena.identity.domain.entity.Address

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
fun Address.toDto(): AddressRequestDto {
    return AddressRequestDto(
        id = id.toString(),
        latitude = latitude,
        longitude = longitude,
        addressType = addressType,
        addressLine = addressLine,
        otherAddressType = otherAddressType,
        isActive = isActive
    )
}