package net.thechance.mena.identity.presentation.screen.profile.components.dialog

interface ShareQrCodeUIEffect {
    data object OnClickDownload: ShareQrCodeUIEffect
    data object OnCopyToClipBoard: ShareQrCodeUIEffect
}