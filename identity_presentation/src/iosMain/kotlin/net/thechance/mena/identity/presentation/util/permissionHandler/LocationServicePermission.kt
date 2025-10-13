package net.thechance.mena.identity.presentation.util.permissionHandler

import net.thechance.mena.identity.presentation.util.permissionHandler.util.openNSUrl
import platform.CoreLocation.CLLocationManager

internal class LocationServicePermission : PermissionController {
    private val locationManager = CLLocationManager()

    override fun getPermissionState(): PermissionState {
        return if (locationManager.locationServicesEnabled())
            PermissionState.GRANTED else PermissionState.DENIED
    }

    override suspend fun providePermission() {
        openSettingPage()
    }

    override fun openSettingPage() {
        openNSUrl("App-Prefs:Privacy&path=LOCATION")
    }
}