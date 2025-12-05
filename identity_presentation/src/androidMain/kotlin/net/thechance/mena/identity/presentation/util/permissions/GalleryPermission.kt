package net.thechance.mena.identity.presentation.util.permissions

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.PermissionManager
import net.thechance.mena.identity.presentation.util.permissions.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.util.permissions.util.handlePermissionState

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

    override suspend fun requestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissionManager.requestPermission(listOf(requiredPermission))
                .values.forEach(::handlePermissionState)
        } else {
            PermissionState.GRANTED
        }
    }

    private fun getWritePermissionStateForSdk29OrBelow(): PermissionState {
        return if (isPermissionsGranted()) {
            PermissionState.GRANTED
        } else {
            PermissionState.DENIED
        }
    }

    private fun isPermissionsGranted(): Boolean {
        return context.checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED
    }
}