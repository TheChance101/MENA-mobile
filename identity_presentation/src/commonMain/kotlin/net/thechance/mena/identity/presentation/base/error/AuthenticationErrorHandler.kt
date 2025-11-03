package net.thechance.mena.identity.presentation.base.error

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.exception.PhoneNumberAlreadyExistsException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException

fun handleAuthenticationException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is InvalidCountryCodeException -> AuthenticationErrorState.InvalidCountryCode
        is InvalidMobileNumberException -> AuthenticationErrorState.InvalidMobileNumber
        is InvalidPasswordException -> AuthenticationErrorState.InvalidPassword
        is InvalidOTPException -> AuthenticationErrorState.InvalidOTP
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is OtpExpiredException -> AuthenticationErrorState.OTPExpired
        is PhoneNumberAlreadyExistsException -> AuthenticationErrorState.PhoneNumberAlreadyExists
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}