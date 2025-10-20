package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.CoordinatesUiState
import org.jetbrains.compose.resources.StringResource
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddEditLocationScreenUIState(
    val addressUIState: AddEditAddressUIState = AddEditAddressUIState(),
    val originalAddressUIState: AddEditAddressUIState = AddEditAddressUIState(),
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val anchorLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(),
    val animateToCurrentLocation: Boolean = false
)

@OptIn(ExperimentalUuidApi::class)
data class AddEditAddressUIState(
    val addressID: Uuid? = null,
    val coordinates: CoordinatesUiState = CoordinatesUiState(28.0,29.0),
    val addressDetails: String = "",
    val addressType: AddressType? = null,
    val otherAddressType: String? = null,
    val isMainAddress: Boolean = false,
)

@OptIn(ExperimentalUuidApi::class)
fun AddEditAddressUIState.toEntity() : Address = Address(
    id = addressID,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude,
    addressLine = addressDetails,
    addressType = if(otherAddressType.isNullOrBlank()) addressType!! else AddressType.Other(otherAddressType),
    isActive = isMainAddress
)