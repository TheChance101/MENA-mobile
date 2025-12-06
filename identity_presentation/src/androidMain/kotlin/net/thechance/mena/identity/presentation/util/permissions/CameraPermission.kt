package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState

internal class CameraPermission(
    private val permissionManager: PermissionManager,
    private val context: Context
) : PermissionController {

    private val requiredPermission = Manifest.permission.CAMERA

    override fun getPermissionState(): PermissionState {
        return if (context.checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
            PermissionState.GRANTED
        } else {
            PermissionState.DENIED
        }
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        if (getPermissionState().isGranted()) return
        permissionManager.requestPermissions(listOf(requiredPermission))
            .values.forEach(::handlePermissionState)
    }
}