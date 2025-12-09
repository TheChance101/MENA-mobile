package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest
import android.content.Context
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
        return permissionManager.checkPermission(requiredPermission)
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = getPermissionState()
        if (beforeStatus.isGranted()) return PermissionState.GRANTED

        val permissionStates = permissionManager.requestPermissions(listOf(requiredPermission)).values
        val afterStatus = handlePermissionState(permissionStates)
        return handleAfterAndBeforePermissionState(afterStatus = afterStatus, beforeStatus = beforeStatus)
    }
}