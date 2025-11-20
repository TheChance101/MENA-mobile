package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.platform.Clipboard
import org.jetbrains.compose.resources.StringResource

interface ShareQrCodeInteractionListener {
    fun onClickDownload(byteArray: ByteArray)
    fun onClickCopyToClipboard(clipboard: Clipboard)
    fun onShowSnackBar(title: StringResource, message: StringResource)
    fun onDismissSnackBar()
}