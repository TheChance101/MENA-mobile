package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import kotlin.coroutines.resume

internal class CameraPermission : PermissionController {

    override fun getPermissionState(): PermissionState {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status.toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override suspend fun requestPermission(): PermissionState {
        val beforeStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

        if (beforeStatus == AVAuthorizationStatusAuthorized) return PermissionState.GRANTED

        return suspendCancellableCoroutine { cont ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) {
                if (cont.isCancelled) return@requestAccessForMediaType

                val afterStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                cont.resume(handleAuthorizationChange(beforeStatus, afterStatus))
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
    ): PermissionState {

        return when {
            afterStatus == AVAuthorizationStatusAuthorized ->
                PermissionState.GRANTED

            beforeStatus == AVAuthorizationStatusNotDetermined &&
                    afterStatus == AVAuthorizationStatusDenied ->
                PermissionState.DENIED

            beforeStatus == AVAuthorizationStatusDenied &&
                    afterStatus == AVAuthorizationStatusDenied ->
                PermissionState.DENIED_PERMANENTLY

            afterStatus == AVAuthorizationStatusRestricted ->
                PermissionState.DENIED_PERMANENTLY

            afterStatus == AVAuthorizationStatusNotDetermined ->
                PermissionState.NOT_DETERMINED

            else ->
                PermissionState.DENIED
        }
    }
}