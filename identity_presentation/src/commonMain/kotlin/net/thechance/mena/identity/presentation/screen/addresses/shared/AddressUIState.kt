package net.thechance.mena.identity.presentation.screen.addresses.shared

import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddressUIState(
    val id: Uuid?,
    val addressType: AddressType = AddressType.Home,
    val isMainAddress: Boolean = false,
    val addressDetails: String = "",
    val coordinates: CoordinatesUiState = CoordinatesUiState(),
    val isDeleting: Boolean = false,
    val isActivating: Boolean = false,
    val isRefreshing: Boolean = false,
)
