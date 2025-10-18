package net.thechance.mena.identity.presentation.util.permissionHandler

enum class PermissionState {
    NOT_DETERMINED,
    GRANTED,
    DENIED;

    fun notGranted(): Boolean {
        return this != GRANTED
    }
}