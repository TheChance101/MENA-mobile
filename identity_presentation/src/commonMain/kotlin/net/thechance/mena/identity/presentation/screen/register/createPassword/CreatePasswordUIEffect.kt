package net.thechance.mena.identity.presentation.screen.register.createPassword

import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState

sealed interface CreatePasswordUIEffect {
    data class NavigateToDatePicker(val registerUIState: RegisterUIState) : CreatePasswordUIEffect
}