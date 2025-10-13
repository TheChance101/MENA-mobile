package net.thechance.mena.identity.presentation.util.permissionHandler

import net.thechance.mena.identity.presentation.util.permissionHandler.util.openAppSettingsPage
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted

internal class LocationForegroundPermission : PermissionController {
    private var locationManager = CLLocationManager()

    override fun getPermissionState(): PermissionState {
        return when (locationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusRestricted -> PermissionState.GRANTED
            kCLAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            kCLAuthorizationStatusDenied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }

    override suspend fun providePermission() {
        locationManager.requestWhenInUseAuthorization()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }
}
