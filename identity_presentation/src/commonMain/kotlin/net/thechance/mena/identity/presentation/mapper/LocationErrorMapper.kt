package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.base.errorState.LocationErrorState
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.error_failed_to_open_settings
import mena.identity_presentation.generated.resources.error_location_is_turned_off
import mena.identity_presentation.generated.resources.error_location_permission_denied
import mena.identity_presentation.generated.resources.error_something_went_wrong
import org.jetbrains.compose.resources.StringResource

internal fun mapLocationErrorToMessage(error: LocationErrorState): StringResource {
    return when (error) {
        LocationErrorState.NoLocationPermission -> Res.string.error_location_permission_denied
        LocationErrorState.UnableToFindLocation -> Res.string.error_location_is_turned_off
        LocationErrorState.FailedToOpenSettings -> Res.string.error_failed_to_open_settings
        LocationErrorState.AddressNotFound -> Res.string.error_address_not_found
        is LocationErrorState.SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}