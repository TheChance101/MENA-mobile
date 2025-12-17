package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject
import kotlin.coroutines.resume

private typealias AuthorizeStateInt = Int

internal class LocationForegroundPermission : PermissionController {

    private val locationManager = CLLocationManager()

    override fun getPermissionState(): PermissionState {
        return locationManager.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = locationManager.authorizationStatus()

        if (beforeStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
            beforeStatus == kCLAuthorizationStatusAuthorizedAlways
        ) return PermissionState.GRANTED


        return suspendCancellableCoroutine { cont ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    cont.resume(handleAuthorizationChange(beforeStatus, manager.authorizationStatus()))
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didChangeAuthorizationStatus: Int
                ) {
                    cont.resume(handleAuthorizationChange(beforeStatus, didChangeAuthorizationStatus))
                }
            }

            locationManager.delegate = delegate
            cont.invokeOnCancellation { locationManager.delegate = null }
            locationManager.requestWhenInUseAuthorization()
        }
    }

    private fun AuthorizeStateInt.toPermissionState(): PermissionState {
        return when (this) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse -> PermissionState.GRANTED
            kCLAuthorizationStatusRestricted -> PermissionState.DENIED_PERMANENTLY
            kCLAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            kCLAuthorizationStatusDenied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }

    private fun handleAuthorizationChange(
        beforeStatus: Int,
        afterStatus: Int,
    ): PermissionState {
        return when {
            afterStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                    afterStatus == kCLAuthorizationStatusAuthorizedAlways -> {
                PermissionState.GRANTED
            }

            beforeStatus == kCLAuthorizationStatusNotDetermined &&
                    afterStatus == kCLAuthorizationStatusDenied -> {
                PermissionState.DENIED
            }

            afterStatus == kCLAuthorizationStatusRestricted -> {
                PermissionState.DENIED_PERMANENTLY
            }

            beforeStatus == kCLAuthorizationStatusDenied &&
                    afterStatus == kCLAuthorizationStatusDenied -> {
                PermissionState.DENIED_PERMANENTLY
            }

            else -> {
                PermissionState.DENIED
            }
        }
    }
}