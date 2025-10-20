package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.base.ErrorState

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.error_cannot_delete_active_address
import mena.identity_presentation.generated.resources.error_failed_to_open_settings
import mena.identity_presentation.generated.resources.error_invalid_country_code
import mena.identity_presentation.generated.resources.error_invalid_credentials
import mena.identity_presentation.generated.resources.error_invalid_mobile_number
import mena.identity_presentation.generated.resources.error_invalid_otp
import mena.identity_presentation.generated.resources.error_invalid_password
import mena.identity_presentation.generated.resources.error_location_permission_denied
import mena.identity_presentation.generated.resources.error_no_network
import mena.identity_presentation.generated.resources.error_otp_expired
import mena.identity_presentation.generated.resources.error_permission_not_granted
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_too_many_requests
import mena.identity_presentation.generated.resources.error_unauthorized
import mena.identity_presentation.generated.resources.error_unknown
import mena.identity_presentation.generated.resources.error_user_blocked
import org.jetbrains.compose.resources.StringResource

internal fun mapErrorToMessage(error: ErrorState): StringResource {
    return when (error) {
        ErrorState.InvalidCredentials -> Res.string.error_invalid_credentials
        ErrorState.InvalidMobileNumber -> Res.string.error_invalid_mobile_number
        ErrorState.InvalidPassword -> Res.string.error_invalid_password
        ErrorState.UserIsBlockedException -> Res.string.error_user_blocked
        ErrorState.LocationPermissionDenied -> Res.string.error_location_permission_denied
        ErrorState.NoNetwork -> Res.string.error_no_network
        ErrorState.TooManyRequests -> Res.string.error_too_many_requests
        ErrorState.InvalidOTP -> Res.string.error_invalid_otp
        ErrorState.OTPExpired -> Res.string.error_otp_expired
        ErrorState.AddressNotFound -> Res.string.error_address_not_found
        is ErrorState.IsActiveAddress -> Res.string.error_cannot_delete_active_address
        is ErrorState.WrongPassword -> Res.string.error_invalid_credentials
        ErrorState.Unknown -> Res.string.error_unknown
        is ErrorState.SomethingWentWrong -> Res.string.error_something_went_wrong
        ErrorState.Unauthorized -> Res.string.error_unauthorized
        ErrorState.FailedToOpenSettings -> Res.string.error_failed_to_open_settings
        ErrorState.FailedToRequestPermission -> Res.string.error_permission_not_granted
        ErrorState.InvalidCountryCode -> Res.string.error_invalid_country_code
        ErrorState.NoLocationPermission -> Res.string.error_location_permission_denied
    }
}

