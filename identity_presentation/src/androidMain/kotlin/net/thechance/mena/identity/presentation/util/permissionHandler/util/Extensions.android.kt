package net.thechance.mena.identity.presentation.util.permissionHandler.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

internal fun Context.openPage(
    action: String,
    newData: Uri? = null,
    onError: (Exception) -> Unit,
) {
    try {
        val intent = Intent(action).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            newData?.let { data = it }
        }
        startActivity(intent)
    } catch (e: Exception) {
        onError(e)
    }
}

internal fun Context.openAppSettingsPage() {
    openPage(
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        newData = Uri.parse("package:$packageName"),
        onError = { throw CannotOpenSettingsException() }
    )
}

internal fun checkPermissions(
    context: Context,
    activity: Lazy<Activity>,
    permissions: List<String>,
): PermissionState {
    permissions.ifEmpty { return PermissionState.GRANTED } // no permissions needed
    val status: List<Int> = permissions.map { context.checkSelfPermission(it) }

    val isAllGranted: Boolean = status.all { it == PackageManager.PERMISSION_GRANTED }
    if (isAllGranted) return PermissionState.GRANTED

    val isAllRequestRationale: Boolean = try {
        permissions.all {
            !activity.value.shouldShowRequestPermissionRationale(it)
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        true
    }
    return if (isAllRequestRationale) PermissionState.NOT_DETERMINED
    else PermissionState.DENIED
}
