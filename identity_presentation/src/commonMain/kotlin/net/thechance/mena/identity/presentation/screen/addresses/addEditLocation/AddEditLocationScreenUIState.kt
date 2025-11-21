package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import org.maplibre.compose.camera.CameraPosition
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddEditLocationScreenUIState(
    val addressUIState: AddEditAddressUIState = AddEditAddressUIState(),
    val originalAddressUIState: AddEditAddressUIState = AddEditAddressUIState(),
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val cameraPosition: CameraPosition = CameraPosition(target = Position(latitude = 29.203231755958047, longitude = 22.39869322710709), zoom = 1.6),
    val animateToCurrentLocation: Boolean = false
){
    data class AddEditAddressUIState(
        val addressID: Uuid? = null,
        val coordinates: CoordinatesUiState = CoordinatesUiState(),
        val addressDetails: String = "",
        val addressType: AddressType? = null,
        val otherAddressType: String? = null,
        val isMainAddress: Boolean = false,
    )
}

