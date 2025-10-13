package net.thechance.mena.identity.presentation.util.permissionHandler

import android.content.Context
import android.location.LocationManager
import android.provider.Settings
import net.thechance.mena.identity.presentation.util.permissionHandler.util.openPage
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException

internal class LocationServicePermission(
    private val context: Context,
    private val locationManager: LocationManager,
) : PermissionController {
    override fun getPermissionState(): PermissionState {
        val granted = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return if (granted)
            PermissionState.GRANTED else PermissionState.DENIED
    }

    override fun openSettingPage() {
        context.openPage(
            action = Settings.ACTION_LOCATION_SOURCE_SETTINGS,
            onError = { throw CannotOpenSettingsException() }
        )
    }
}