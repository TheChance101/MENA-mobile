package net.thechance.mena.identity.presentation.screen.register.createPassword

import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import org.jetbrains.compose.resources.StringResource

sealed interface CreatePasswordUIEffect {
    data class NavigateToDatePicker(val registerUIState: RegisterUIState) : CreatePasswordUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : CreatePasswordUIEffect
}