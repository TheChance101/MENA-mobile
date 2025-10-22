package net.thechance.mena.core_chat.presentation.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.launch
import net.thechance.mena.core_chat.presentation.utils.encodeToByteArrayWithCompressionToMaxSize

@Composable
actual fun rememberCameraManager(onResult: (ByteArray?) -> Unit): CameraManager {
    val scope = rememberCoroutineScope()
    val launcher = rememberCameraPickerLauncher { file ->
        file?.let { image ->
            scope.launch {
                onResult(image.toImageBitmap().encodeToByteArrayWithCompressionToMaxSize())
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