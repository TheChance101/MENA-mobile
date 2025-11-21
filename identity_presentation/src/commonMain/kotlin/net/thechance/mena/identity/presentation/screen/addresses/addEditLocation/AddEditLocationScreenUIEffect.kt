package net.thechance.mena.identity.presentation.screen.addresses.addEditLocation

import net.thechance.mena.identity.presentation.screen.addresses.shared.AddressUIState
import org.jetbrains.compose.resources.StringResource

sealed interface AddEditLocationScreenUIEffect {
    data class NavigateBack(
        val errorStringResource: StringResource? = null,
        val successStringResource: StringResource? = null
    ) : AddEditLocationScreenUIEffect

    data class NavigateToMap(
        val addressModel: AddressUIState? = null,
        val onUpdateLocation: (AddressUIState) -> Unit
    ) : AddEditLocationScreenUIEffect
}