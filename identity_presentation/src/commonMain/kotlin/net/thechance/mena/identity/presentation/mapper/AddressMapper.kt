package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.AddressUIState
import net.thechance.mena.identity.presentation.screen.addresses.CoordinatesUiState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Address.toEntity() : AddressUIState = AddressUIState(
    id =id,
    addressType = AddressType.valueOf(addressType),
    isMainAddress = isActive,
    addressDetails = addressLine,
    coordinates = CoordinatesUiState(latitude, longitude)
)
@OptIn(ExperimentalUuidApi::class)
fun AddressUIState.toEntity() : Address = Address(
    id = id,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude,
    addressLine = addressDetails,
    addressType = addressType.name,
    otherAddressType =null,
    isActive = isMainAddress
)