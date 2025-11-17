package net.thechance.mena.identity.presentation.feature.profileFlow.profile.components.dialog

interface ShareQrCodeUIEffect {
    data object OnClickDownload: ShareQrCodeUIEffect
    data object OnCopyToClipBoard: ShareQrCodeUIEffect
}