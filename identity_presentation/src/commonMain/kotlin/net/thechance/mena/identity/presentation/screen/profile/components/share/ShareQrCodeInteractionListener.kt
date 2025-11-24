package net.thechance.mena.identity.presentation.screen.profile.components.share

import androidx.compose.ui.platform.Clipboard

interface ShareQrCodeInteractionListener {
    fun onClickDownload(byteArray: ByteArray)
    fun onClickCopyToClipboard(clipboard: Clipboard)
}