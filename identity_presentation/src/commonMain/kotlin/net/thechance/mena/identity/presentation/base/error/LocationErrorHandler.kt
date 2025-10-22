package net.thechance.mena.identity.presentation.base.error

import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import net.thechance.mena.identity.domain.exception.FailedToRequestPermissionException
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException

fun handleLocationException(
    exception: LocationException,
    onError: (LocationErrorState) -> Unit,
) {
    when (exception) {
        is UnableToFindLocationException -> onError(LocationErrorState.UnableToFindLocation)
        is CannotOpenSettingsException -> onError(LocationErrorState.FailedToOpenSettings)
        is FailedToRequestPermissionException -> onError(LocationErrorState.FailedToRequestPermission)
        is AddressNotFoundException -> onError(LocationErrorState.AddressNotFound)
        else -> onError(LocationErrorState.SomethingWentWrong(exception.message))
    }
}