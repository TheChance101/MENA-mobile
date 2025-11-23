package net.thechance.mena.identity.presentation.screen.profile

import org.jetbrains.compose.resources.StringResource


sealed interface ProfileScreenUIEffect {
    object NavigateToEditProfileScreen : ProfileScreenUIEffect
    object NavigateToLocationPickerScreen : ProfileScreenUIEffect
    data object NavigateToChangePasswordScreen : ProfileScreenUIEffect

    object NavigateToPrivacyAndPolicyScreen : ProfileScreenUIEffect
    object NavigateContactUsScreen : ProfileScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ProfileScreenUIEffect
}