package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens

sealed interface SelectGenderScreenUIEffect {
    data class NavigateToUploadProfileImage(
        val authTokens: AuthenticationTokens,
        val phoneNumber: PhoneNumber
    ) : SelectGenderScreenUIEffect
}