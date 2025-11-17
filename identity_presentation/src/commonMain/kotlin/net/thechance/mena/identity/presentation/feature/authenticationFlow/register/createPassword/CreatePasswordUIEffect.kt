package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.createPassword

import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.shared.uiState.RegisterUIState

sealed interface CreatePasswordUIEffect {
    data class NavigateToDatePicker(val registerUIState: RegisterUIState) : CreatePasswordUIEffect
}