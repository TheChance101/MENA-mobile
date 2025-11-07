package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.Clipboard

interface ShareQrCodeInteractionListener {
    fun onClickDownload(bitmap: ImageBitmap)
    fun onClickCopyToClipboard(clipboard: Clipboard)
    fun onDismissShareDialog()
    fun onDismissCopyLinkSnackBar()
}