package net.thechance.mena.identity.presentation.screen.profile

sealed interface ProfileScreenUIEffect {
    object NavigateToEditProfileScreen : ProfileScreenUIEffect
    object NavigateToLocationPickerScreen : ProfileScreenUIEffect
    object NavigateToChangePasswordScreen : ProfileScreenUIEffect
    object NavigateToPrivacyAndPolicyScreen : ProfileScreenUIEffect
    object NavigateContactUsScreen : ProfileScreenUIEffect
}