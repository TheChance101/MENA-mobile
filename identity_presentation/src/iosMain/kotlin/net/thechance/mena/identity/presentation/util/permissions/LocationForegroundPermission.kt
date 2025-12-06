package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
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
import kotlin.coroutines.resumeWithException

private typealias AuthorizeStateInt = Int

internal class LocationForegroundPermission : PermissionController {
    private var locationManager = CLLocationManager()

    override fun getPermissionState(): PermissionState {
        return locationManager.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        if (getPermissionState().isGranted()) return

        suspendCancellableCoroutine { cont ->
            locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    if (!cont.isActive) return
                    when (getPermissionState()) {
                        PermissionState.GRANTED -> cont.resume(Unit)
                        PermissionState.DENIED -> cont.resumeWithException(PermissionDeniedException())
                        PermissionState.DENIED_PERMANENTLY -> cont.resumeWithException(PermissionDeniedPermanentlyException())
                        PermissionState.NOT_DETERMINED -> {}
                    }
                }
            }
            cont.invokeOnCancellation { locationManager.delegate = null }
            locationManager.requestWhenInUseAuthorization()
        }
    }

    private fun AuthorizeStateInt.toPermissionState(): PermissionState {
        return when (this) {
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusRestricted -> PermissionState.GRANTED
            kCLAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            kCLAuthorizationStatusDenied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }
}