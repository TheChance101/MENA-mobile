package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.ImageBitmap

interface ShareQrCodeInteractionListener {
    fun onClickDownload(bitmap: ImageBitmap)
}