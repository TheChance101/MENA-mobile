package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.CoreLocation.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private typealias AuthorizeStateInt = Int

internal class LocationForegroundPermission : PermissionController {

    private val locationManager = CLLocationManager()

    override fun getPermissionState(): PermissionState {
        return locationManager.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        val beforeStatus = locationManager.authorizationStatus()

        if (beforeStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
            beforeStatus == kCLAuthorizationStatusAuthorizedAlways
        ) return

        return suspendCancellableCoroutine { cont ->
            locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    if (!cont.isActive) return

                    val afterStatus = manager.authorizationStatus()
                    when {
                        afterStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                                afterStatus == kCLAuthorizationStatusAuthorizedAlways -> {
                            cont.resume(Unit)
                        }

                        beforeStatus == kCLAuthorizationStatusNotDetermined &&
                                afterStatus == kCLAuthorizationStatusDenied -> {
                            cont.resumeWithException(PermissionDeniedException())
                        }

                        afterStatus == kCLAuthorizationStatusRestricted -> {
                            cont.resumeWithException(PermissionDeniedPermanentlyException())
                        }

                        beforeStatus == kCLAuthorizationStatusDenied &&
                                afterStatus == kCLAuthorizationStatusDenied -> {
                            cont.resumeWithException(PermissionDeniedPermanentlyException())
                        }

                        else -> {
                            cont.resumeWithException(PermissionDeniedException())
                        }
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
            kCLAuthorizationStatusAuthorizedWhenInUse -> PermissionState.GRANTED
            kCLAuthorizationStatusRestricted -> PermissionState.DENIED_PERMANENTLY
            kCLAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            kCLAuthorizationStatusDenied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }
}