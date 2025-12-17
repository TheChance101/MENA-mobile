package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handleAfterAndBeforePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage

internal class GalleryPermission(
    private val context: Context,
    private val permissionManager: PermissionManager
) : PermissionController {
    private val requiredPermission = WRITE_EXTERNAL_STORAGE

    override fun getPermissionState(): PermissionState {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getWritePermissionStateForSdk29OrBelow()
        } else {
            PermissionState.GRANTED
        }
    }

    override fun openSettingPage() {
        context.openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val beforeStatus = getPermissionState()
            if (beforeStatus.isGranted()) return PermissionState.GRANTED

            val permissionStates = permissionManager.requestPermissions(listOf(requiredPermission)).values
            val afterStatus = handlePermissionState(permissionStates)
            handleAfterAndBeforePermissionState(afterStatus = afterStatus, beforeStatus = beforeStatus)
        } else {
            PermissionState.GRANTED
        }
    }

    private fun getWritePermissionStateForSdk29OrBelow(): PermissionState {
        return if (isPermissionsGranted()) {
            PermissionState.GRANTED
        } else {
            return permissionManager.checkPermission(requiredPermission)
        }
    }

    private fun isPermissionsGranted(): Boolean {
        return context.checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED
    }
}