package net.thechance.mena.identity.presentation.util

import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionController
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHPhotoLibrary

private typealias AuthorizeStateLong = Long

internal class GalleryPermission : PermissionController {

    override fun getPermissionState(): PermissionState {
        return PHPhotoLibrary.authorizationStatus().toPermissionState()
    }

    override fun openSettingPage() {
        openAppSettingsPage()
    }

    override fun requestPermission() {
        PHPhotoLibrary.requestAuthorization {}
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