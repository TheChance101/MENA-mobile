package net.thechance.mena.identity.presentation.screen.profile

import net.thechance.mena.identity.domain.entity.User
import org.jetbrains.compose.resources.StringResource


sealed interface ProfileScreenUIEffect {
    data class NavigateToEditProfileScreen(
       val  userInfo: User? = null
    ): ProfileScreenUIEffect
    object NavigateToLocationPickerScreen : ProfileScreenUIEffect
    data object NavigateToChangePasswordScreen : ProfileScreenUIEffect

    object NavigateToPrivacyAndPolicyScreen : ProfileScreenUIEffect
    object NavigateContactUsScreen : ProfileScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ProfileScreenUIEffect
}