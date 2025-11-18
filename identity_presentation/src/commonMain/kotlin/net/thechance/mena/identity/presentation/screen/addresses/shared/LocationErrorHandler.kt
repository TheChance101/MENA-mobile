package net.thechance.mena.identity.presentation.screen.addresses.shared

import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import net.thechance.mena.identity.domain.exception.LocationException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState
import net.thechance.mena.identity.presentation.base.errorState.LocationErrorState

fun handleLocationException(
    exception: LocationException,
): LocationErrorState {
    return when (exception) {
        is UnableToFindLocationException -> LocationErrorState.UnableToFindLocation
        is CannotOpenSettingsException -> LocationErrorState.FailedToOpenSettings
        is AddressNotFoundException -> LocationErrorState.AddressNotFound
        else -> LocationErrorState.SomethingWentWrong(exception.message)
    }
}

fun handleLocationAuthenticationException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}