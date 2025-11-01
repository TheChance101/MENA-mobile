package net.thechance.mena.identity.presentation.screen.register.createPassword

sealed interface CreatePasswordUIEffect {
    data object NavigateBack : CreatePasswordUIEffect
    // TODO: Add navigation to next screen after password creation
}