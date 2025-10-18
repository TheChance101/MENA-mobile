package net.thechance.mena.identity.presentation.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resume

actual class PermissionManager() {
    companion object {
        private var activityRef: WeakReference<ComponentActivity>? = null

        lateinit var launcher: ActivityResultLauncher<String>

        lateinit var onResult: (Boolean) -> Unit

        fun init(activity: ComponentActivity) {
            activityRef = WeakReference(activity)

            launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                onResult(isGranted)
            }
        }
    }

    actual suspend fun requestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val activity = activityRef?.get()
            ?: throw IllegalStateException("PermissionManager not initialized. Call PermissionManager.init(activity) from an Activity.")

        suspendCancellableCoroutine { continuation ->
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                    continuation.resume(Unit)
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA
                ) -> {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity.packageName, null)
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    activity.startActivity(intent)

                    continuation.resume(Unit)
                }

                else -> {
                    launcher.launch(Manifest.permission.CAMERA)

                    onResult = { isGranted ->
                        if (isGranted) {
                            onGranted()
                        } else {
                            onDenied()
                        }
                        continuation.resume(Unit)
                    }
                }
            }
        }
    }
}
