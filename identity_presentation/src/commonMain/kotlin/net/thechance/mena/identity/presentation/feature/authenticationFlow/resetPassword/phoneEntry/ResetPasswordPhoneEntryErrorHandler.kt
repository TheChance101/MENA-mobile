package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.phoneEntry

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState

fun handleResetPasswordPhoneEntryException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is InvalidCountryCodeException -> AuthenticationErrorState.InvalidCountryCode
        is InvalidCredentialsException -> AuthenticationErrorState.InvalidMobileNumber
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}