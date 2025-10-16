package net.thechance.mena.identity.presentation.base

import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.exception.InvalidCountryCodeException
import net.thechance.mena.identity.domain.exception.InvalidCredentialsException
import net.thechance.mena.identity.domain.exception.InvalidMobileNumberException
import net.thechance.mena.identity.domain.exception.InvalidOTPException
import net.thechance.mena.identity.domain.exception.InvalidPasswordException
import net.thechance.mena.identity.domain.exception.NoNetworkException
import net.thechance.mena.identity.domain.exception.OtpExpiredException
import net.thechance.mena.identity.domain.exception.TooManyRequestsException
import net.thechance.mena.identity.domain.exception.UserIsBlockedException

sealed interface ErrorState {
    // region Authorization
    data object InvalidCountryCode : ErrorState
    data object InvalidMobileNumber : ErrorState
    data object InvalidPassword : ErrorState
    data object UserIsBlockedException : ErrorState
    data class WrongPassword(val message: String) : ErrorState
    data object InvalidCredentials : ErrorState
    data object Unauthorized : ErrorState
    data object Unknown : ErrorState
    data object LocationPermissionDenied : ErrorState
    data object InvalidOTP : ErrorState
    data object TooManyRequests : ErrorState
    object NoNetwork : ErrorState
    data object OTPExpired : ErrorState
    // endregion

    data class SomethingWentWrong(val message: String?) : ErrorState
    data class IsActiveAddress(val message: String?) : ErrorState
}

fun handelAuthorizationException(
    exception: AuthenticationException,
    onError: (t: ErrorState) -> Unit,
) {
    when (exception) {
        is InvalidCountryCodeException -> onError(ErrorState.InvalidCountryCode)
        is InvalidMobileNumberException -> onError(ErrorState.InvalidMobileNumber)
        is InvalidPasswordException -> onError(ErrorState.InvalidPassword)
        is UserIsBlockedException -> onError(ErrorState.UserIsBlockedException)
        is InvalidCredentialsException -> onError(ErrorState.WrongPassword(exception.message ?: ""))
        is InvalidOTPException -> onError(ErrorState.InvalidOTP)
        is TooManyRequestsException -> onError(ErrorState.TooManyRequests)
        is OtpExpiredException -> onError(ErrorState.OTPExpired)
        is NoNetworkException -> onError(ErrorState.NoNetwork)
        else -> onError(ErrorState.SomethingWentWrong(exception.message))
    }
}
