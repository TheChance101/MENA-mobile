package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.error_invalid_otp
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_username_required
import net.thechance.mena.identity.presentation.base.errorState.AuthenticationErrorState
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.base.errorState.LocationErrorState
import net.thechance.mena.identity.presentation.base.errorState.ProfileErrorState
import org.junit.Test
import kotlin.test.assertEquals

class ErrorMapperTest {

    @Test
    fun `mapErrorToMessage should call mapAuthenticationErrorToMessage for AuthenticationError`() {
        val error = ErrorState.AuthenticationError(AuthenticationErrorState.InvalidOTP)

        val result = mapErrorToMessage(error)

        assertEquals(Res.string.error_invalid_otp, result)
    }

    @Test
    fun `mapErrorToMessage should call mapLocationErrorToMessage for LocationError`() {
        val error = ErrorState.LocationError(LocationErrorState.AddressNotFound)

        val result = mapErrorToMessage(error)

        assertEquals(Res.string.error_address_not_found, result)
    }

    @Test
    fun `mapErrorToMessage should call mapProfileErrorToMessage for ProfileError`() {
        val error = ErrorState.ProfileError(ProfileErrorState.UsernameRequired)

        val result = mapErrorToMessage(error)

        assertEquals(Res.string.error_username_required, result)
    }

    @Test
    fun `mapErrorToMessage should return error_something_went_wrong for GenericError`() {
        val error = ErrorState.GenericError(Exception("Test"))

        val result = mapErrorToMessage(error)

        assertEquals(Res.string.error_something_went_wrong, result)
    }
}