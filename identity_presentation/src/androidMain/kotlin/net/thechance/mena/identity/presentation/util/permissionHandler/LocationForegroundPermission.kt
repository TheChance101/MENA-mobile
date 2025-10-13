package net.thechance.mena.identity.presentation.util.permissionHandler

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import net.thechance.mena.identity.presentation.util.permissionHandler.util.openAppSettingsPage

internal class LocationForegroundPermission(
    private val context: Context,


    ) : PermissionController {
    override fun getPermissionState(): PermissionState {
        if (fineLocationPermissions.isEmpty()) return PermissionState.GRANTED
        val allGranted = fineLocationPermissions.all {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
        return if (allGranted) PermissionState.GRANTED else PermissionState.DENIED
    }

    override suspend fun providePermission() {
        TODO("not implemented")
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
