package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import org.jetbrains.compose.resources.StringResource

data class ShareQrCodeUIState(
    val showDialog: Boolean = false,
    val showCopiedMessage: Boolean = false,
    val shareLinkUrl: String = "",
    val fullName: String = "",
    val errorMessage: StringResource? = null
)