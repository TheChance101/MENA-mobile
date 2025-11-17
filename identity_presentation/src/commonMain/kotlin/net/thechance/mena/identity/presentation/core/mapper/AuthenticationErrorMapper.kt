package net.thechance.mena.identity.presentation.core.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_bad_request
import mena.identity_presentation.generated.resources.error_incorrect_password
import mena.identity_presentation.generated.resources.error_invalid_country_code
import mena.identity_presentation.generated.resources.error_invalid_credentials
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
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.IncorrectPassword
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidCountryCode
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidCredentials
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidMobileNumber
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidOTP
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidPassword
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.InvalidRequest
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.NoNetwork
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.OTPExpired
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.PhoneNumberAlreadyExists
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.SomethingWentWrong
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.TooManyRequests
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.UserIsBlocked
import net.thechance.mena.identity.presentation.core.base.errorState.AuthenticationErrorState.UsernameAlreadyExists
import org.jetbrains.compose.resources.StringResource

internal fun mapAuthenticationErrorToMessage(error: AuthenticationErrorState): StringResource {
    return when (error) {
        InvalidCountryCode -> Res.string.error_invalid_country_code
        InvalidMobileNumber -> Res.string.error_invalid_mobile_number
        InvalidPassword -> Res.string.error_invalid_password
        InvalidOTP -> Res.string.error_invalid_otp
        UserIsBlocked -> Res.string.error_user_blocked
        TooManyRequests -> Res.string.error_too_many_requests
        OTPExpired -> Res.string.error_otp_expired
        PhoneNumberAlreadyExists -> Res.string.error_phone_number_already_exists
        UsernameAlreadyExists -> Res.string.error_username_already_exists
        NoNetwork -> Res.string.error_no_network
        InvalidRequest -> Res.string.error_bad_request
        IncorrectPassword -> Res.string.error_incorrect_password
        InvalidCredentials -> Res.string.error_invalid_credentials
        is SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}