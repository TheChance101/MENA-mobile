package net.thechance.mena.identity.presentation.util.permissions.util


import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

fun handlePermissionState(states: Collection<PermissionState>): PermissionState {
    return when {
        states.any { it == PermissionState.DENIED_PERMANENTLY } -> PermissionState.DENIED_PERMANENTLY
        states.any { it == PermissionState.DENIED } -> PermissionState.DENIED
        states.any { it == PermissionState.NOT_DETERMINED } -> PermissionState.NOT_DETERMINED
        states.all { it == PermissionState.GRANTED } -> PermissionState.GRANTED
        else -> PermissionState.DENIED
    }
}

fun handleAfterAndBeforePermissionState(
    afterStatus: PermissionState,
    beforeStatus: PermissionState
): PermissionState {
    return when {
        afterStatus.isGranted() -> PermissionState.GRANTED
        beforeStatus == PermissionState.DENIED && afterStatus == PermissionState.DENIED_PERMANENTLY -> PermissionState.DENIED
        beforeStatus == PermissionState.DENIED_PERMANENTLY && afterStatus == PermissionState.DENIED_PERMANENTLY -> PermissionState.DENIED_PERMANENTLY
        else -> PermissionState.DENIED
    }
}