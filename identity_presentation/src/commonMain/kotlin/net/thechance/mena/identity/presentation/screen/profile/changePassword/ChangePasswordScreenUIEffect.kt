package net.thechance.mena.identity.presentation.screen.profile.changePassword

import org.jetbrains.compose.resources.StringResource


sealed interface ChangePasswordScreenUIEffect {
    data class NavigateBack(val successStringResource: StringResource? = null) : ChangePasswordScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ChangePasswordScreenUIEffect
}