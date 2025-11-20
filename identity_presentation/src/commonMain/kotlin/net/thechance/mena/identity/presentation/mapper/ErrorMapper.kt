package net.thechance.mena.identity.presentation.mapper

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_something_went_wrong
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.base.errorState.ErrorState.AuthenticationError
import net.thechance.mena.identity.presentation.base.errorState.ErrorState.GenericError
import net.thechance.mena.identity.presentation.base.errorState.ErrorState.LocationError
import net.thechance.mena.identity.presentation.base.errorState.ErrorState.ProfileError
import org.jetbrains.compose.resources.StringResource

fun mapErrorToMessage(error: ErrorState): StringResource {
    return when (error) {
        is AuthenticationError -> mapAuthenticationErrorToMessage(error.errorState)
        is LocationError -> mapLocationErrorToMessage(error.errorState)
        is ProfileError -> mapProfileErrorToMessage(error.errorState)
        is GenericError -> Res.string.error_something_went_wrong
    }
}