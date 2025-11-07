package net.thechance.mena.identity.presentation.screen.register.enterName

import net.thechance.mena.identity.domain.entity.PhoneNumber

sealed interface EnterNameUIEffect {
    data class NavigateToPassword(
        val phoneNumber: PhoneNumber,
        val firstName: String,
        val lastName: String,
        val username: String
    ) : EnterNameUIEffect
}