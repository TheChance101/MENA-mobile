package net.thechance.mena.identity.presentation.util.permissions.util

import net.thechance.mena.identity.domain.exception.PermissionDeniedException
import net.thechance.mena.identity.domain.exception.PermissionDeniedPermanentlyException
import net.thechance.mena.identity.domain.exception.PermissionNotDeterminedException
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState

fun handlePermissionState(state: PermissionState) {
    when (state) {
        PermissionState.GRANTED -> {}
        PermissionState.NOT_DETERMINED -> throw PermissionNotDeterminedException()
        PermissionState.DENIED -> throw PermissionDeniedException()
        PermissionState.DENIED_PERMANENTLY -> throw PermissionDeniedPermanentlyException()
    }
}