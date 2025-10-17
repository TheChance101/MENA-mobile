package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.base.ErrorState

internal fun mapErrorToMessage(error: ErrorState): String {
    return when (error) {
        ErrorState.InvalidCountryCode -> "Invalid country code"
        ErrorState.InvalidMobileNumber -> "Invalid mobile number"
        ErrorState.InvalidPassword -> "Invalid password"
        ErrorState.UserIsBlockedException -> "Too many failed attempts. Please try again later."
        is ErrorState.WrongPassword -> error.message
        ErrorState.LocationPermissionDenied -> "Location permission denied"
        ErrorState.InvalidCredentials -> "Invalid credentials. Please check your phone number and password."
        ErrorState.Unknown -> "Something went wrong. Please try again later."
        ErrorState.Unauthorized -> "You don’t have permission to access the app."
        is ErrorState.SomethingWentWrong -> "Something went wrong"
        ErrorState.InvalidOTP -> "Incorrect OTP."
        ErrorState.TooManyRequests -> "Too many requests. Please try again later."
        ErrorState.OTPExpired -> "OTP expired"
        ErrorState.NoNetwork -> "No Internet Connection"
        is ErrorState.IsActiveAddress -> "Can't Delete Active Address"
    }
}

