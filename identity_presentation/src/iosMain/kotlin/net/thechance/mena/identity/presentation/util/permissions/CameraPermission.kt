package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.AVFoundation.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class CameraPermission : PermissionController {

    override fun getPermissionState(): PermissionState {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status.toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission() {
        val beforeStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

        if (beforeStatus == AVAuthorizationStatusAuthorized) return

        return suspendCancellableCoroutine { cont ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                if (cont.isCancelled) return@requestAccessForMediaType

                val afterStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                handleAuthorizationChange(beforeStatus, afterStatus, cont, granted)
                }
            }
        }
    }

    private fun AVAuthorizationStatus.toPermissionState(): PermissionState {
        return when (this) {
            AVAuthorizationStatusAuthorized -> PermissionState.GRANTED
            AVAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            AVAuthorizationStatusDenied -> PermissionState.DENIED
            AVAuthorizationStatusRestricted -> PermissionState.DENIED_PERMANENTLY
            else -> PermissionState.NOT_DETERMINED
        }
    }

    private fun handleAuthorizationChange(
        beforeStatus: AVAuthorizationStatus,
        afterStatus: AVAuthorizationStatus,
        cont: CancellableContinuation<Unit>,
        granted: Boolean
    ){
        when {
            granted || afterStatus == AVAuthorizationStatusAuthorized -> {
                cont.resume(Unit)
            }

            beforeStatus == AVAuthorizationStatusNotDetermined &&
                    afterStatus == AVAuthorizationStatusDenied -> {
                cont.resumeWithException(PermissionDeniedException())
            }

            beforeStatus == AVAuthorizationStatusDenied &&
                    afterStatus == AVAuthorizationStatusDenied -> {
                cont.resumeWithException(PermissionDeniedPermanentlyException())
            }

            afterStatus == AVAuthorizationStatusRestricted -> {
                cont.resumeWithException(PermissionDeniedPermanentlyException())
            }

            afterStatus == AVAuthorizationStatusNotDetermined -> {
                cont.resumeWithException(PermissionNotDeterminedException())
            }

            else -> {
                cont.resumeWithException(PermissionDeniedException())
            }
    }
}
