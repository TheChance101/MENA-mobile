package net.thechance.mena.identity.presentation.screen.login

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState

fun handleLoginException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is InvalidCountryCodeException -> AuthenticationErrorState.InvalidCountryCode
        is InvalidMobileNumberException -> AuthenticationErrorState.InvalidMobileNumber
        is InvalidPasswordException -> AuthenticationErrorState.InvalidPassword
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        is InvalidCredentialsException -> AuthenticationErrorState.InvalidCredentials
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}