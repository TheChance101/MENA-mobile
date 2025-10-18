package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.addresses.AddressRequestDto
import net.thechance.mena.identity.data.dto.addresses.AddressResponseDto
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressTypeFromString

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Address.toDto(): AddressRequestDto {
    return AddressRequestDto(
        id = id.toString(),
        latitude = latitude,
        longitude = longitude,
        addressType = addressType.getAddressType(),
        addressLine = addressLine,
        isActive = isActive
    )
}

@OptIn(ExperimentalUuidApi::class)
fun AddressResponseDto.toEntity(): Address {
    return Address(
        id = Uuid.parse(id),
        latitude = latitude,
        longitude = longitude,
        addressType = getAddressTypeFromString(addressType),
        addressLine = addressLine,
        isActive = isActive
    )
}