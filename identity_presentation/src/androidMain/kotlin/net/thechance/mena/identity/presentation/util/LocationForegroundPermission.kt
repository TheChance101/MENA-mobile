package net.thechance.mena.identity.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

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

internal val fineLocationPermissions: List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    } else {
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }