package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberCameraManager(onResult: (ImageBitmap?) -> Unit): CameraManager


expect class CameraManager{
    fun launch()
}