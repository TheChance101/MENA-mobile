package net.thechance.mena.identity.presentation.screen.profile.components.share

data class ShareQrCodeUIState(
    val showDialog: Boolean = false,
    val isLoading: Boolean = false,
    val shareLinkUrl: String = "",
)