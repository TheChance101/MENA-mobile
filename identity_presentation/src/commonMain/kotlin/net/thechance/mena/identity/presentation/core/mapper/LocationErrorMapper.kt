package net.thechance.mena.identity.presentation.core.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_address_not_found
import mena.identity_presentation.generated.resources.error_failed_to_open_settings
import mena.identity_presentation.generated.resources.error_location_permission_denied
import mena.identity_presentation.generated.resources.error_something_went_wrong
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState.AddressNotFound
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState.FailedToOpenSettings
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState.NoLocationPermission
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState.SomethingWentWrong
import net.thechance.mena.identity.presentation.core.base.errorState.LocationErrorState.UnableToFindLocation
import org.jetbrains.compose.resources.StringResource

internal fun mapLocationErrorToMessage(error: LocationErrorState): StringResource {
    return when (error) {
        NoLocationPermission -> Res.string.error_location_permission_denied
        UnableToFindLocation -> Res.string.error_location_permission_denied
        FailedToOpenSettings -> Res.string.error_failed_to_open_settings
        AddressNotFound -> Res.string.error_address_not_found
        is SomethingWentWrong -> Res.string.error_something_went_wrong
    }
}