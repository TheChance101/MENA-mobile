package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.addresses.request.AddressRequestDto
import net.thechance.mena.identity.data.dto.addresses.response.AddressResponseDto
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressType
import net.thechance.mena.identity.domain.entity.AddressType.AddressTypeMapper.getAddressTypeFromString
import net.thechance.mena.identity.domain.model.AddressInput
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Address.toDto(id: String? = null, isActive: Boolean = false): AddressRequestDto {
    return AddressRequestDto(
        id = id,
        latitude = latitude,
        longitude = longitude,
        addressType = addressType.getAddressType(),
        addressLine = addressLine,
        isActive = isActive
    )
}

fun AddressInput.toDto(id: String? = null, isActive: Boolean = false): AddressRequestDto {
    return AddressRequestDto(
        id = id,
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
        addressLine = addressLine
    )
}