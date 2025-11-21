package net.thechance.mena.identity.presentation.screen.profile.components.dialog

data class ShareQrCodeUIState(
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val shareLinkUrl: String = "",
)