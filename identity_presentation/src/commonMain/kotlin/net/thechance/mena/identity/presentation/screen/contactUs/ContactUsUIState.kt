package net.thechance.mena.identity.presentation.screen.contactUs

import kotlinx.coroutines.flow.MutableSharedFlow

data class ContactUsUIState(
    val urlToOpen: MutableSharedFlow<String> = MutableSharedFlow(),
    val displayedEmail: String = "MENA2025@gmail.com",
    val displayedPhoneNumber: String = "+964 770 0000 000",
    val displayedFacebookAccount: String = "MENA-THE-CHANCE",
    val email: String = "MENA2025@gmail.com",
    val phoneNumber: String = "+9647700000000",
    val facebookUrl: String = "https://www.facebook.com",
)