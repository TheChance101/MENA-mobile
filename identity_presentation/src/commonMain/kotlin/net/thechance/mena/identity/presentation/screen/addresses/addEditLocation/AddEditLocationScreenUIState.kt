package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import androidx.compose.ui.unit.DpOffset
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.CoordinatesUiState
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
){
    data class AddEditAddressUIState(
        val addressID: Uuid? = null,
        val coordinates: CoordinatesUiState = CoordinatesUiState(28.0,29.0),
        val addressDetails: String = "",
        val addressType: AddressType? = null,
        val otherAddressType: String? = null,
        val isMainAddress: Boolean = false,
    )
}

