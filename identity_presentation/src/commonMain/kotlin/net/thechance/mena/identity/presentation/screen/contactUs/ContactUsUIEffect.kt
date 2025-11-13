package net.thechance.mena.identity.presentation.screen.contactUs

sealed interface ContactUsUIEffect {
    object NavigateBack : ContactUsUIEffect
    data class OpenUrl(val url: String) : ContactUsUIEffect
}