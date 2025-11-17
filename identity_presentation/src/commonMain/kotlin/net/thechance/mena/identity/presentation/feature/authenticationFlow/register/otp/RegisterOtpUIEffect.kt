package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.otp

import net.thechance.mena.identity.domain.entity.PhoneNumber

sealed interface RegisterOtpUIEffect {
    data class NavigateToEnterName(val phoneNumber: PhoneNumber) : RegisterOtpUIEffect
}