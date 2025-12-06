package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
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
import kotlin.coroutines.resumeWithException

private typealias AuthorizeStateLong = Long

internal class GalleryPermission : PermissionController {

    override fun getPermissionState(): PermissionState {
        return PHPhotoLibrary.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        val beforeStatus = PHPhotoLibrary.authorizationStatus()

        if (beforeStatus == PHAuthorizationStatusAuthorized ||
            beforeStatus == PHAuthorizationStatusLimited
        ) return

        return suspendCancellableCoroutine { cont ->

            PHPhotoLibrary.requestAuthorization { afterStatus ->
                if (cont.isCancelled) return@requestAuthorization
                handleAuthorizationChange(beforeStatus, afterStatus, cont)
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
        cont: CancellableContinuation<Unit>
    ){
        when {
            afterStatus == PHAuthorizationStatusAuthorized ||
                    afterStatus == PHAuthorizationStatusLimited -> {
                cont.resume(Unit)
            }

            beforeStatus == PHAuthorizationStatusNotDetermined &&
                    afterStatus == PHAuthorizationStatusDenied -> {
                cont.resumeWithException(PermissionDeniedException())
            }

            beforeStatus == PHAuthorizationStatusDenied &&
                    afterStatus == PHAuthorizationStatusDenied -> {
                cont.resumeWithException(PermissionDeniedPermanentlyException())
            }

            afterStatus == PHAuthorizationStatusNotDetermined -> {
                cont.resumeWithException(PermissionNotDeterminedException())
            }

            else -> {
                cont.resumeWithException(PermissionDeniedException())
            }
        }
    }
}
