package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.StringResource

interface ShareQrCodeInteractionListener {
    fun onClickDownload(
        painter: Painter,
        screenSize: IntSize,
        density: Density,
        layoutDirection: LayoutDirection
    )

    fun onClickCopyToClipboard(clipboard: Clipboard)
    fun onShowSnackBar(title: StringResource, message: StringResource)
}