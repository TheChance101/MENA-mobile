package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.enterName

import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.shared.uiState.RegisterUIState

sealed interface EnterNameUIEffect {
    data class NavigateToPassword(val registerUIState: RegisterUIState) : EnterNameUIEffect
}