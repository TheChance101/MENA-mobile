package net.thechance.mena.identity.presentation.screen.addresses

import net.thechance.mena.identity.domain.entity.AddressType

import androidx.compose.ui.unit.DpOffset
import org.maplibre.compose.camera.CameraPosition

data class AddLocationScreenUIState(
    val addressID: String? = null,
    val latitude: Double = 28.0,
    val longitude: Double = 29.0,
    val address: String = "",
    val addressType: AddressType? = null,
    val otherAddress: String? = null,
    val originalAddress: String = "",
    val originalAddressType: AddressType? = null,
    val originalOtherAddress: String? = null,
    val isActive: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val anchorLocation: DpOffset? = null,
    val cameraPosition: CameraPosition = CameraPosition(),
    val animateToCurrentLocation: Boolean = false
)