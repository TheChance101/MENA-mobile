package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_camera_permission_required
import mena.identity_presentation.generated.resources.error_first_name_required
import mena.identity_presentation.generated.resources.error_last_name_required
import mena.identity_presentation.generated.resources.error_no_network
import mena.identity_presentation.generated.resources.error_password_mismatch
import mena.identity_presentation.generated.resources.error_something_went_wrong
import mena.identity_presentation.generated.resources.error_username_required
import net.thechance.mena.identity.presentation.base.errorState.ProfileErrorState
import org.junit.Test
import kotlin.test.assertEquals

class ProfileErrorMapperTest {

    @Test
    fun `mapProfileErrorToMessage should return error_username_required for UsernameRequired`() {
        val error = ProfileErrorState.UsernameRequired

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_username_required, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_first_name_required for FirstNameRequired`() {
        val error = ProfileErrorState.FirstNameRequired

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_first_name_required, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_last_name_required for LastNameRequired`() {
        val error = ProfileErrorState.LastNameRequired

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_last_name_required, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_password_mismatch for PasswordMismatch`() {
        val error = ProfileErrorState.PasswordMismatch

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_password_mismatch, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_camera_permission_required for CameraPermissionRequired`() {
        val error = ProfileErrorState.CameraPermissionRequired

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_camera_permission_required, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_no_network for NoNetwork`() {
        val error = ProfileErrorState.NoNetwork

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_no_network, result)
    }

    @Test
    fun `mapProfileErrorToMessage should return error_something_went_wrong for SomethingWentWrong`() {
        val error = ProfileErrorState.SomethingWentWrong("Test")

        val result = mapProfileErrorToMessage(error)

        assertEquals(Res.string.error_something_went_wrong, result)
    }
}