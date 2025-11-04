package net.thechance.mena.identity.presentation.screen.register.selectGender

sealed interface SelectGenderScreenUIEffect {
    data object NavigateToAccountCreatedScreen : SelectGenderScreenUIEffect
}