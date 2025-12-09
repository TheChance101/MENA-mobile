package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handleAfterAndBeforePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage

internal class LocationForegroundPermission(
    private val context: Context,
    private val permissionManager: PermissionManager
) : PermissionController {

    override fun getPermissionState(): PermissionState {
        val states = fineLocationPermissions.map { permissionManager.checkPermission(it) }
        return handlePermissionState(states)
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = getPermissionState()
        if (beforeStatus.isGranted()) return PermissionState.GRANTED

        val permissionStates = permissionManager.requestPermissions(fineLocationPermissions).values
        val afterStatus = handlePermissionState(permissionStates)
        return handleAfterAndBeforePermissionState(afterStatus = afterStatus, beforeStatus = beforeStatus)
    }
}

private val fineLocationPermissions: List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }