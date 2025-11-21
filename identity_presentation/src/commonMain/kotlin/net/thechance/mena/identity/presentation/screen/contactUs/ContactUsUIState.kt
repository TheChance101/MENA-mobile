package net.thechance.mena.identity.presentation.screen.contactUs

import kotlinx.coroutines.flow.MutableSharedFlow

data class ContactUsUIState(
    val isLoading: Boolean = true,
    val urlToOpen: MutableSharedFlow<String> = MutableSharedFlow(),
    val displayedFacebookAccount: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val facebookUrl: String = "",
)