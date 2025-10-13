package net.thechance.mena.identity.presentation.util.permissionHandler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import net.thechance.mena.identity.presentation.util.permissionHandler.util.checkPermissions
import net.thechance.mena.identity.presentation.util.permissionHandler.util.openAppSettingsPage
import net.thechance.mena.identity.domain.exception.FailedToRequestPermissionException

internal class LocationForegroundPermission(
    private val context: Context,
    private val activity: Lazy<Activity>,


) : PermissionController {
    override fun getPermissionState(): PermissionState {
        return checkPermissions(context, activity, fineLocationPermissions)
    }

    override suspend fun providePermission() {
        try {
            ActivityCompat.requestPermissions(
                activity.value, fineLocationPermissions.toTypedArray(), 100
            )
        } catch (_: Throwable) {
            throw FailedToRequestPermissionException()
        }
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }
}

internal val fineLocationPermissions: List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
