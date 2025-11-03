package net.thechance.mena.identity.presentation.screen.register.enterName

sealed interface EnterNameUIEffect {
    data object NavigateBack : EnterNameUIEffect
    data object NavigateToNextStep : EnterNameUIEffect
}