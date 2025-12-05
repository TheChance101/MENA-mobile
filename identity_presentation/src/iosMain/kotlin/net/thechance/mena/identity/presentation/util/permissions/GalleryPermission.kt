package net.thechance.mena.identity.presentation.util.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.openAppSettingsPage
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
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
        return suspendCancellableCoroutine { cont ->
            PHPhotoLibrary.requestAuthorization { _ ->
                if (cont.isCancelled) return@requestAuthorization

                when (getPermissionState()) {
                    PermissionState.GRANTED -> cont.resume(Unit)
                    PermissionState.NOT_DETERMINED -> cont.resumeWithException(PermissionNotDeterminedException())
                    PermissionState.DENIED -> cont.resumeWithException(PermissionDeniedException())
                    PermissionState.DENIED_PERMANENTLY -> cont.resumeWithException(PermissionDeniedPermanentlyException())
                }
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
}