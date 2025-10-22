package net.thechance.mena.identity.presentation.base.error

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException

fun handleAuthenticationException(
    exception: AuthenticationException,
    onError: (AuthenticationErrorState) -> Unit,
) {
    when (exception) {
        is InvalidCountryCodeException -> onError(AuthenticationErrorState.InvalidCountryCode)
        is InvalidMobileNumberException -> onError(AuthenticationErrorState.InvalidMobileNumber)
        is InvalidPasswordException -> onError(AuthenticationErrorState.InvalidPassword)
        is InvalidOTPException -> onError(AuthenticationErrorState.InvalidOTP)
        is UserIsBlockedException -> onError(AuthenticationErrorState.UserIsBlocked)
        is TooManyRequestsException -> onError(AuthenticationErrorState.TooManyRequests)
        is OtpExpiredException -> onError(AuthenticationErrorState.OTPExpired)
        is NoNetworkException -> onError(AuthenticationErrorState.NoNetwork)
        else -> onError(AuthenticationErrorState.SomethingWentWrong(exception.message))
    }
}