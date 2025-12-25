package net.thechance.mena.identity.presentation.screen.register.otp

import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import org.jetbrains.compose.resources.StringResource

sealed interface RegisterOtpUIEffect {
    data class NavigateToEnterName(val registerUIState: RegisterUIState) : RegisterOtpUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : RegisterOtpUIEffect
}