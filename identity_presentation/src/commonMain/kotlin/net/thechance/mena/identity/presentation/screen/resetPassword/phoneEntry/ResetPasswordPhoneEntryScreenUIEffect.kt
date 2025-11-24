package net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry

import org.jetbrains.compose.resources.StringResource

sealed interface ResetPasswordPhoneEntryScreenUIEffect {
    data class NavigateToOTP(
        val phoneNumber: String,
        val callingCode: String,
        val countryCode: String
    ) : ResetPasswordPhoneEntryScreenUIEffect

    data object NavigateBack : ResetPasswordPhoneEntryScreenUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) :
        ResetPasswordPhoneEntryScreenUIEffect
}