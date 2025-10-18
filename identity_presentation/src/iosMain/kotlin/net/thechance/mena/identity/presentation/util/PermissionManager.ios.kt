package net.thechance.mena.identity.presentation.util

import kotlinx.cinterop.autoreleasepool
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import kotlin.coroutines.resume

actual class PermissionManager {
    actual suspend fun requestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        suspendCancellableCoroutine { cont ->
            autoreleasepool {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                when (status) {
                    AVAuthorizationStatusAuthorized -> {
                        onGranted()
                        cont.resume(Unit)
                    }

                    AVAuthorizationStatusNotDetermined -> {
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                            if (granted) {
                                onGranted()
                            } else {
                                onDenied()
                            }
                            cont.resume(Unit)
                        }
                    }

                    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                        onDenied()
                        cont.resume(Unit)
                    }

                    else -> {
                        onDenied()
                        cont.resume(Unit)
                    }
                }
            }
        }
    }
}
