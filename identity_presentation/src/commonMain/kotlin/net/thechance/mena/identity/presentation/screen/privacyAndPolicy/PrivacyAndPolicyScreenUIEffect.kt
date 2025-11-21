package net.thechance.mena.identity.presentation.screen.privacyAndPolicy

import org.jetbrains.compose.resources.StringResource

sealed interface PrivacyAndPolicyScreenUIEffect {

    data object NavigateBack : PrivacyAndPolicyScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : PrivacyAndPolicyScreenUIEffect
}