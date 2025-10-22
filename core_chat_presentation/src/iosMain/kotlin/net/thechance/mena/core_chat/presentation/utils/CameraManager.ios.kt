package net.thechance.mena.core_chat.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.launch

@Composable
actual fun rememberCameraManager(onResult: (ImageBitmap?) -> Unit): CameraManager {
    val scope = rememberCoroutineScope()
    val launcher = rememberCameraPickerLauncher { file ->
        if (file == null) {
            onResult(null)
        } else {
            scope.launch {
                onResult(file.toImageBitmap())
            }
        }
    }
    return CameraManager(launcher)
}

actual class CameraManager(val launcher: PhotoResultLauncher) {
    actual fun launch() {
        launcher.launch()
    }
}