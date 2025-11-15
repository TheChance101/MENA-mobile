package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState

fun handleSelectGenderException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        is PhoneNumberAlreadyExistsException -> AuthenticationErrorState.UsernameAlreadyExists
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}