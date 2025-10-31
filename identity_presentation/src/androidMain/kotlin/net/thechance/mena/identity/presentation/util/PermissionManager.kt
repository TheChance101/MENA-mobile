package net.thechance.mena.identity.presentation.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import java.lang.ref.WeakReference

class PermissionManager() {
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

    fun requestPermission(permissions: List<String>, onResult: (Map<String, PermissionState>) -> Unit) {
        Companion.onResult = onResult
        launcher.launch(permissions.toTypedArray())
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
