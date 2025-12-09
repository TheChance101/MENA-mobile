package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handleAfterAndBeforePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage

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

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = getPermissionState()
        if (beforeStatus.isGranted()) return PermissionState.GRANTED

        val afterStatus = handlePermissionState(permissionManager.requestPermissions(listOf(requiredPermission)).values)
        return handleAfterAndBeforePermissionState(afterStatus = afterStatus, beforeStatus = beforeStatus)
    }
}