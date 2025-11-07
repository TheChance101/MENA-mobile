package net.thechance.mena.identity.presentation.screen.register.createPassword

import net.thechance.mena.identity.domain.entity.PhoneNumber

sealed interface CreatePasswordUIEffect {
    data class NavigateToDatePicker(
        val phoneNumber: PhoneNumber,
        val firstName: String,
        val lastName: String,
        val username: String,
        val password: String
    ) : CreatePasswordUIEffect
}