package net.thechance.mena.identity.presentation.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import java.lang.ref.WeakReference
import kotlin.coroutines.resume

class PermissionManager {
    companion object {
        lateinit var launcher: ActivityResultLauncher<Array<String>>
        lateinit var onResult: (Map<String, PermissionState>) -> Unit

        fun init(activity: ComponentActivity) {
            val activityRef = WeakReference(activity)

            launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                val permissionStates = results.mapValues { (permission, granted) ->
                    resolvePermissionState(granted, activityRef.get(), permission)
                }
                onResult(permissionStates)
            }
        }
    }

    suspend fun requestPermission(permissions: List<String>): Map<String, PermissionState> {
        return suspendCancellableCoroutine { continuation ->
            launcher.launch(permissions.toTypedArray())
            onResult = { results ->
                continuation.resume(results)
            }
        }
    }
}

private fun resolvePermissionState(
    isGranted: Boolean,
    activity: ComponentActivity?,
    permission: String
): PermissionState {
    return when {
        isGranted -> PermissionState.GRANTED

        activity != null &&
                !shouldShowRequestPermissionRationale(activity, permission) ->
            PermissionState.DENIED_PERMANENTLY

        else -> PermissionState.DENIED
    }
}
