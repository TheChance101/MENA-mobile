package net.thechance.mena.identity.presentation.screen.addresses

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.delete_address_description
import mena.identity_presentation.generated.resources.delete_address_title
import mena.identity_presentation.generated.resources.error
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class AddressesScreenUIState(
    val addresses: List<AddressUIState> = emptyList(),
    val deleteDialogUIState: DeleteDialogUIState = DeleteDialogUIState(),
    val errorMessage: String? = null,
    val snackBarUiState: SnackBarUiState = SnackBarUiState(),
    val animateToCurrentLocation: Boolean = false,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalUuidApi::class)
data class DeleteDialogUIState(
    val title: StringResource = Res.string.delete_address_title,
    val description: StringResource = Res.string.delete_address_description,
    val addressId: Uuid? = null,
    val isVisible: Boolean = false
)

@OptIn(ExperimentalUuidApi::class)
data class AddressUIState(
    val id: Uuid?,
    val addressType: AddressType = AddressType.Home,
    val isMainAddress: Boolean = false,
    val addressDetails: String = "",
    val coordinates: CoordinatesUiState = CoordinatesUiState(),
)

data class CoordinatesUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

data class SnackBarUiState(
    val isVisible: Boolean = false,
    val snackBarType: SnackBarType = SnackBarType.ERROR,
    val message: StringResource = Res.string.error,
)

enum class SnackBarType {
    ERROR,
    SUCCESS,
}

@OptIn(ExperimentalUuidApi::class)
fun Address.toUiState(): AddressUIState {
    return AddressUIState(
        id = this.id,
        addressType = addressType,
        isMainAddress = this.isActive,
        addressDetails = this.addressLine,
        coordinates = CoordinatesUiState(this.latitude, this.longitude),
    )
}


