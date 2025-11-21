package net.thechance.mena.identity.presentation.screen.register.otp

import net.thechance.mena.identity.domain.entity.PhoneNumber
import org.jetbrains.compose.resources.StringResource

sealed interface RegisterOtpUIEffect {
    data class NavigateToEnterName(val phoneNumber: PhoneNumber) : RegisterOtpUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : RegisterOtpUIEffect
}