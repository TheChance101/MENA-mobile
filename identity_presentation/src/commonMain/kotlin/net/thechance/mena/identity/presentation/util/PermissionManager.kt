package net.thechance.mena.identity.presentation.util

expect class PermissionManager {
    suspend fun requestCameraPermission(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    )
}