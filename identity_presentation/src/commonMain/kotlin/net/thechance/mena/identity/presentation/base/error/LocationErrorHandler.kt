package net.thechance.mena.identity.presentation.base.error

import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import net.thechance.mena.identity.domain.exception.FailedToRequestPermissionException
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException

fun handleLocationException(
    exception: LocationException,
): LocationErrorState {
    return when (exception) {
        is UnableToFindLocationException -> LocationErrorState.UnableToFindLocation
        is CannotOpenSettingsException -> LocationErrorState.FailedToOpenSettings
        is FailedToRequestPermissionException -> LocationErrorState.FailedToRequestPermission
        is AddressNotFoundException -> LocationErrorState.AddressNotFound
        else -> LocationErrorState.SomethingWentWrong(exception.message)
    }
}