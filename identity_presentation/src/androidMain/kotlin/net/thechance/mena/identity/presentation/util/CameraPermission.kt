package net.thechance.mena.identity.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

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
        permissionManager.requestPermission(listOf(requiredPermission))
            .values.forEach(::handlePermissionResult)
    }

    private fun handlePermissionResult(state: PermissionState) {
        when (state) {
            PermissionState.NOT_DETERMINED -> throw PermissionNotDeterminedException()
            PermissionState.GRANTED -> {}
            PermissionState.DENIED -> throw PermissionDeniedException()
            PermissionState.DENIED_PERMANENTLY -> throw PermissionDeniedPermanentlyException()
        }
    }
}