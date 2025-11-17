package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.phoneEntry

import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.shared.uiState.RegisterUIState

sealed interface RegisterPhoneEntryUIEffect {
    data class NavigateToOTP(val registerUIState: RegisterUIState) : RegisterPhoneEntryUIEffect
    data object NavigateToLogin : RegisterPhoneEntryUIEffect
}