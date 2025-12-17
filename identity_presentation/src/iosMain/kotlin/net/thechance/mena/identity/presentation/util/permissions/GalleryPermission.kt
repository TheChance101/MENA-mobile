package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.AVFoundation.AVAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import kotlin.coroutines.resume

private typealias AuthorizeStateLong = Long

internal class GalleryPermission : PermissionController {

    override fun getPermissionState(): PermissionState {
        return PHPhotoLibrary.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = PHPhotoLibrary.authorizationStatus()

        if (beforeStatus == PHAuthorizationStatusAuthorized ||
            beforeStatus == PHAuthorizationStatusLimited
        ) return PermissionState.GRANTED

        return suspendCancellableCoroutine { cont ->

            PHPhotoLibrary.requestAuthorization { afterStatus ->
                if (cont.isCancelled) return@requestAuthorization
                cont.resume(handleAuthorizationChange(beforeStatus, afterStatus))
            }
        }
    }

    private fun AuthorizeStateLong.toPermissionState(): PermissionState {
        return when (this) {
            PHAuthorizationStatusAuthorized -> PermissionState.GRANTED
            PHAuthorizationStatusLimited -> PermissionState.GRANTED
            PHAuthorizationStatusDenied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }

    private fun handleAuthorizationChange(
        beforeStatus: AVAuthorizationStatus,
        afterStatus: AVAuthorizationStatus,
    ): PermissionState{
        return when {
            afterStatus == PHAuthorizationStatusAuthorized ||
                    afterStatus == PHAuthorizationStatusLimited -> {
                PermissionState.GRANTED
            }

            beforeStatus == PHAuthorizationStatusNotDetermined &&
                    afterStatus == PHAuthorizationStatusDenied -> {
                PermissionState.DENIED
            }

            beforeStatus == PHAuthorizationStatusDenied &&
                    afterStatus == PHAuthorizationStatusDenied -> {
                PermissionState.DENIED_PERMANENTLY
            }

            afterStatus == PHAuthorizationStatusNotDetermined -> {
                PermissionState.NOT_DETERMINED
            }

            else -> {
                PermissionState.DENIED
            }
        }
    }
}
