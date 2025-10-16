package net.thechance.mena.core_chat.presentation.camera

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberCameraManager(onResult: (ByteArray?) -> Unit): CameraManager {
    val context = LocalContext.current
    val contentResolver: ContentResolver = context.contentResolver
    var tempPhotoUri: Uri by remember { mutableStateOf(Uri.EMPTY) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onResult.invoke(
                        tempPhotoUri.getBitmapFromUri(contentResolver).toByteArray()
                )
            } else {
                onResult.invoke(null)
            }
        }
    )
    return remember {
        CameraManager(
            onLaunch = {
                ComposeFileProvider.getImageUri(context)?.let {
                    tempPhotoUri = it
                    cameraLauncher.launch(tempPhotoUri)
                } ?: run { onResult.invoke(null) }
            }
        )
    }
}

actual class CameraManager actual constructor(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}


