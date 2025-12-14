package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.error_failed_to_open_settings
import mena.identity_presentation.generated.resources.error_location_is_turned_off
import mena.identity_presentation.generated.resources.error_location_permission_denied
import mena.identity_presentation.generated.resources.error_something_went_wrong
import net.thechance.mena.identity.presentation.base.errorState.LocationErrorState
import org.junit.Test
import kotlin.test.assertEquals

class LocationErrorMapperTest {

    @Test
    fun `mapLocationErrorToMessage should return error_location_permission_denied for NoLocationPermission`() {
        val error = LocationErrorState.NoLocationPermission

        val result = mapLocationErrorToMessage(error)

        assertEquals(Res.string.error_location_permission_denied, result)
    }

    @Test
    fun `mapLocationErrorToMessage should return error_location_permission_denied for UnableToFindLocation`() {
        val error = LocationErrorState.UnableToFindLocation

        val result = mapLocationErrorToMessage(error)

        assertEquals(Res.string.error_location_is_turned_off, result)
    }

    @Test
    fun `mapLocationErrorToMessage should return error_failed_to_open_settings for FailedToOpenSettings`() {
        val error = LocationErrorState.FailedToOpenSettings

        val result = mapLocationErrorToMessage(error)

        assertEquals(Res.string.error_failed_to_open_settings, result)
    }

    @Test
    fun `mapLocationErrorToMessage should return error_address_not_found for AddressNotFound`() {
        val error = LocationErrorState.AddressNotFound

        val result = mapLocationErrorToMessage(error)

        assertEquals(Res.string.error_address_not_found, result)
    }

    @Test
    fun `mapLocationErrorToMessage should return error_something_went_wrong for SomethingWentWrong`() {
        val error = LocationErrorState.SomethingWentWrong("Test")

        val result = mapLocationErrorToMessage(error)

        assertEquals(Res.string.error_something_went_wrong, result)
    }
}