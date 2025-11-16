package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_bad_request
import mena.identity_presentation.generated.resources.error_incorrect_password
import mena.identity_presentation.generated.resources.error_invalid_country_code
import mena.identity_presentation.generated.resources.error_invalid_mobile_number
import mena.identity_presentation.generated.resources.error_invalid_otp
import mena.identity_presentation.generated.resources.error_invalid_password
import mena.identity_presentation.generated.resources.error_no_network
import mena.identity_presentation.generated.resources.error_otp_expired
import mena.identity_presentation.generated.resources.error_phone_number_already_exists
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_too_many_requests
import mena.identity_presentation.generated.resources.error_user_blocked
import mena.identity_presentation.generated.resources.error_username_already_exists
import mena.identity_presentation.generated.resources.error_invalid_credentials
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState
import org.jetbrains.compose.resources.StringResource

internal fun mapAuthenticationErrorToMessage(error: AuthenticationErrorState): StringResource {
    return when (error) {
        AuthenticationErrorState.InvalidCountryCode -> Res.string.error_invalid_country_code
        AuthenticationErrorState.InvalidMobileNumber -> Res.string.error_invalid_mobile_number
        AuthenticationErrorState.InvalidPassword -> Res.string.error_invalid_password
        AuthenticationErrorState.InvalidOTP -> Res.string.error_invalid_otp
        AuthenticationErrorState.UserIsBlocked -> Res.string.error_user_blocked
        AuthenticationErrorState.TooManyRequests -> Res.string.error_too_many_requests
        AuthenticationErrorState.OTPExpired -> Res.string.error_otp_expired
        AuthenticationErrorState.PhoneNumberAlreadyExists -> Res.string.error_phone_number_already_exists
        AuthenticationErrorState.UsernameAlreadyExists -> Res.string.error_username_already_exists
        AuthenticationErrorState.NoNetwork -> Res.string.error_no_network
        AuthenticationErrorState.InvalidRequest -> Res.string.error_bad_request
        AuthenticationErrorState.IncorrectPassword -> Res.string.error_incorrect_password
        AuthenticationErrorState.InvalidCredentials -> Res.string.error_invalid_credentials
        is AuthenticationErrorState.SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}