package net.thechance.mena.identity.presentation.screen.register.otp

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidRequestException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UnAuthorizedException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState

fun handleRegisterOtpException(
    exception: AuthenticationException,
): AuthenticationErrorState {
    return when (exception) {
        is UserIsBlockedException -> AuthenticationErrorState.UserIsBlocked
        is UnAuthorizedException -> AuthenticationErrorState.InvalidOTP
        is InvalidRequestException -> AuthenticationErrorState.OTPExpired
        is TooManyRequestsException -> AuthenticationErrorState.TooManyRequests
        is NoNetworkException -> AuthenticationErrorState.NoNetwork
        else -> AuthenticationErrorState.SomethingWentWrong(exception.message)
    }
}