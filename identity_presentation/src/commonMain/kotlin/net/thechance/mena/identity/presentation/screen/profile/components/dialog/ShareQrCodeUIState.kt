package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import org.jetbrains.compose.resources.StringResource

data class ShareQrCodeUIState(
    val qrCodeUrl: String? = null,
    val errorMessage: StringResource? = null
)