package net.thechance.mena.identity.presentation.screen.register.enterName

import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState

sealed interface EnterNameUIEffect {
    data class NavigateToPassword(val registerUIState: RegisterUIState) : EnterNameUIEffect
}