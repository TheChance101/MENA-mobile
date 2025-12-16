package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen

import net.thechance.mena.identity.presentation.screen.profile.shared.UserUIState
import org.jetbrains.compose.resources.StringResource


sealed interface ProfileScreenUIEffect {
    data class NavigateToEditProfileScreen(
       val  userInfo: UserUIState
    ): ProfileScreenUIEffect
    object NavigateToLocationPickerScreen : ProfileScreenUIEffect
    data object NavigateToChangePasswordScreen : ProfileScreenUIEffect

    object NavigateToPrivacyAndPolicyScreen : ProfileScreenUIEffect
    object NavigateContactUsScreen : ProfileScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ProfileScreenUIEffect
}