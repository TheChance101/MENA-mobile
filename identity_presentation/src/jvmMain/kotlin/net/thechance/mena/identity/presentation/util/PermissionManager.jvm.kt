package net.thechance.mena.identity.presentation.util

actual class PermissionManager {
    actual suspend fun requestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        // No need for permission in desktop
        onGranted()
    }
}