package net.thechance.mena.identity.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class CameraImagePicker {
    fun launch()
}

@Composable
expect fun rememberCameraPicker(onImagePicked: (ImageBitmap) -> Unit): CameraImagePicker
