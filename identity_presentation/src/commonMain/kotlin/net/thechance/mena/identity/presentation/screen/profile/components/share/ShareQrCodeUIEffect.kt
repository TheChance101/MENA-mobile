package net.thechance.mena.identity.presentation.screen.profile.components.share

import org.jetbrains.compose.resources.StringResource

interface ShareQrCodeUIEffect {
    data object ShowClickDownloadSnackBar : ShareQrCodeUIEffect
    data object ShowCopyToClipBoardSnackBar : ShareQrCodeUIEffect

    data class ShowSnackBarError(val errorStringResource: StringResource) : ShareQrCodeUIEffect
}