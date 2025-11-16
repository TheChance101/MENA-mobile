package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState

fun handleRegisterPhoneEntryException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is InvalidCountryCodeException -> AuthenticationErrorState.InvalidCountryCode
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        is InvalidRequestException -> AuthenticationErrorState.InvalidMobileNumber
        is PhoneNumberAlreadyExistsException -> AuthenticationErrorState.PhoneNumberAlreadyExists
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}