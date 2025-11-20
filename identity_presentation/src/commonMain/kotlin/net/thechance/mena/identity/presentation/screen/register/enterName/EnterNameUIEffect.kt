package net.thechance.mena.identity.presentation.screen.register.enterName

import net.thechance.mena.identity.presentation.screen.register.shared.uiState.RegisterUIState
import org.jetbrains.compose.resources.StringResource

sealed interface EnterNameUIEffect {
    data class NavigateToPassword(val registerUIState: RegisterUIState) : EnterNameUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : EnterNameUIEffect
}