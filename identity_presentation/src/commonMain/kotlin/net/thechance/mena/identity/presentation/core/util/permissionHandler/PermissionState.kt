package net.thechance.mena.identity.presentation.core.util.permissionHandler

enum class PermissionState {
    NOT_DETERMINED,
    GRANTED,
    DENIED,
    DENIED_PERMANENTLY;

    fun notGranted(): Boolean {
        return this != GRANTED
    }
}