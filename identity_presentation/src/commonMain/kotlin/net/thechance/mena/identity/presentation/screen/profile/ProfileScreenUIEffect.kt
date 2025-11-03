package net.thechance.mena.identity.presentation.screen.profile

import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.SnackBarUiState

sealed interface ProfileScreenUIEffect {
    object NavigateToEditProfileScreen : ProfileScreenUIEffect
    object NavigateToLocationPickerScreen : ProfileScreenUIEffect
    data class NavigateToChangePasswordScreen(val onSuccess: (SnackBarUiState?) -> Unit) : ProfileScreenUIEffect
    object NavigateToPrivacyAndPolicyScreen : ProfileScreenUIEffect
    object NavigateContactUsScreen : ProfileScreenUIEffect
}