package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_bad_request
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
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState
import org.junit.Test
import kotlin.test.assertEquals

class AuthenticationErrorMapperTest {

    @Test
    fun `mapAuthenticationErrorToMessage should return error_invalid_country_code for InvalidCountryCode`() {
        val error = AuthenticationErrorState.InvalidCountryCode

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_invalid_country_code, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_invalid_mobile_number for InvalidMobileNumber`() {
        val error = AuthenticationErrorState.InvalidMobileNumber

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_invalid_mobile_number, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_invalid_password for InvalidPassword`() {
        val error = AuthenticationErrorState.InvalidPassword

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_invalid_password, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_invalid_otp for InvalidOTP`() {
        val error = AuthenticationErrorState.InvalidOTP

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_invalid_otp, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_user_blocked for UserIsBlocked`() {
        val error = AuthenticationErrorState.UserIsBlocked

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_user_blocked, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_too_many_requests for TooManyRequests`() {
        val error = AuthenticationErrorState.TooManyRequests

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_too_many_requests, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_otp_expired for OTPExpired`() {
        val error = AuthenticationErrorState.OTPExpired

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_otp_expired, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_phone_number_already_exists for PhoneNumberAlreadyExists`() {
        val error = AuthenticationErrorState.PhoneNumberAlreadyExists

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_phone_number_already_exists, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_username_already_exists for UsernameAlreadyExists`() {
        val error = AuthenticationErrorState.UsernameAlreadyExists

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_username_already_exists, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_no_network for NoNetwork`() {
        val error = AuthenticationErrorState.NoNetwork

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_no_network, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_bad_request for InvalidRequest`() {
        val error = AuthenticationErrorState.InvalidRequest

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_bad_request, result)
    }

    @Test
    fun `mapAuthenticationErrorToMessage should return error_something_went_wrong for SomethingWentWrong`() {
        val error = AuthenticationErrorState.SomethingWentWrong("Test")

        val result = mapAuthenticationErrorToMessage(error)

        assertEquals(Res.string.error_something_went_wrong, result)
    }
}