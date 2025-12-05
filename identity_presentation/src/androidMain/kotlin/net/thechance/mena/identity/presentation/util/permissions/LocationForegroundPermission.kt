package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState

internal class LocationForegroundPermission(
    private val context: Context,
    private val permissionManager: PermissionManager
) : PermissionController {

    override fun getPermissionState(): PermissionState {
        if (fineLocationPermissions.isEmpty()) return PermissionState.GRANTED
        val allGranted = fineLocationPermissions.all {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
        return if (allGranted) PermissionState.GRANTED else PermissionState.DENIED
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        permissionManager.requestPermission(fineLocationPermissions)
            .values.forEach(::handlePermissionState)
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