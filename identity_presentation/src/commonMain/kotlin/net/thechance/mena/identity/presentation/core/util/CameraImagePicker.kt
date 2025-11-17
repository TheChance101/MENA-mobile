package net.thechance.mena.identity.presentation.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class CameraImagePicker {
    fun launch()
}

@Composable
expect fun rememberCameraPicker(onImagePicked: (ImageBitmap) -> Unit): CameraImagePicker
