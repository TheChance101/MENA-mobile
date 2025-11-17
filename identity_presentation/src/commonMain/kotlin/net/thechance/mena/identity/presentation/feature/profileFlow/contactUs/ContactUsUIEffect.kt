package net.thechance.mena.identity.presentation.feature.profileFlow.contactUs

sealed interface ContactUsUIEffect {
    object NavigateBack : ContactUsUIEffect
    data class OpenUrl(val url: String) : ContactUsUIEffect
}