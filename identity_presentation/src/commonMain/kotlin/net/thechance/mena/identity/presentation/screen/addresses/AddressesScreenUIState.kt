package net.thechance.mena.identity.presentation.screen.addresses

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.delete_address_description
import mena.identity_presentation.generated.resources.delete_address_title
import mena.identity_presentation.generated.resources.error
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import org.jetbrains.compose.resources.StringResource

data class AddressesScreenUIState(
    val addresses: List<AddressUIState> = emptyList(),
    val addressToDelete: Long? = null,
    val deleteAddressDialogUIState: DeleteAddressDialogUIState = DeleteAddressDialogUIState(),
    val errorMessage: String? = null,
    val snackBarUiState :SnackBarUiState= SnackBarUiState()
)

data class DeleteAddressDialogUIState(
    val title: StringResource = Res.string.delete_address_title,
    val description: StringResource = Res.string.delete_address_description,
    val addressId: Long = -1,
    val isVisible: Boolean = false
)

data class AddressUIState(
    val id: Long,
    val addressType: AddressType = AddressType.HOME,
    val isMainAddress: Boolean = false,
    val addressDetails: String = "",
    val currentLocation: CoordinatesUiState = CoordinatesUiState(),
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
fun Address.toUiState(): AddressUIState {
    return AddressUIState(
        id = this.id,
        addressType = this.addressType,
        isMainAddress = this.isMainAddress,
        addressDetails = this.addressDetails,
        currentLocation = CoordinatesUiState(this.latitude, this.longitude),
    )
}


