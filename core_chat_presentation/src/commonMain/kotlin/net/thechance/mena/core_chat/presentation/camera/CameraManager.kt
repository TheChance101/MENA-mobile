package net.thechance.mena.core_chat.presentation.camera

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraManager(onResult: (ByteArray?) -> Unit): CameraManager


expect class CameraManager(
    onLaunch: () -> Unit
) {
    fun launch()
}