package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.StringResource

sealed interface AddEditLocationScreenUIEffect {
    data object NavigateBack : AddEditLocationScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        AddEditLocationScreenUIEffect

    data class ShowSnackBarSuccess(val successStringResource: StringResource) :
        AddEditLocationScreenUIEffect

    data class NavigateToMap(
        val addressModel: AddressUIState? = null,
        val onUpdateLocation: (AddressUIState) -> Unit
    ) : AddEditLocationScreenUIEffect
}