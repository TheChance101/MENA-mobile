package net.thechance.mena.identity.presentation.util

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import kotlin.coroutines.resume

class PermissionManager {

    companion object {
        private lateinit var launcher: ActivityResultLauncher<Array<String>>
        private var onResult: ((Map<String, PermissionState>) -> Unit)? = null
        private var activityRef: ComponentActivity? = null

        fun init(activity: ComponentActivity) {
            activityRef = activity

            launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                val activity = activityRef
                val states = results.mapValues { (permission, granted) ->
                    resolvePermissionState(granted, activity, permission)
                }
                onResult?.invoke(states)
            }
        }
    }

    suspend fun requestPermissions(permissions: List<String>): Map<String, PermissionState> {
        return suspendCancellableCoroutine { continuation ->
            onResult = { results ->
                continuation.resume(results)
            }
            launcher.launch(permissions.toTypedArray())
        }
    }

    fun checkPermission(permission: String): PermissionState {
        val activity = activityRef
        return resolvePermissionState(
            isGranted = activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED,
            activity = activity,
            permission = permission
        )
    }
}

private fun resolvePermissionState(
    isGranted: Boolean,
    activity: ComponentActivity?,
    permission: String
): PermissionState {
    return when {
        isGranted -> PermissionState.GRANTED

        activity != null && !shouldShowRequestPermissionRationale(activity, permission) ->
            PermissionState.DENIED_PERMANENTLY

        else -> PermissionState.DENIED
    }
}
