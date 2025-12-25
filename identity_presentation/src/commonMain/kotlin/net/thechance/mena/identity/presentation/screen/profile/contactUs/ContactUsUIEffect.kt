package net.thechance.mena.identity.presentation.screen.profile.contactUs

import org.jetbrains.compose.resources.StringResource

sealed interface ContactUsUIEffect {
    object NavigateBack : ContactUsUIEffect
    data class OpenUrl(val url: String) : ContactUsUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ContactUsUIEffect
}