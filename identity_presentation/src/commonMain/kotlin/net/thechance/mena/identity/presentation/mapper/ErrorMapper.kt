package net.thechance.mena.identity.presentation.mapper

import net.thechance.mena.identity.presentation.base.error.AuthenticationErrorState
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.LocationErrorState
import net.thechance.mena.identity.presentation.base.error.ProfileErrorState
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_something_went_wrong
import org.jetbrains.compose.resources.StringResource

fun mapErrorToMessage(error: Any): StringResource {
    return when (error) {
        is AuthenticationErrorState -> mapAuthenticationErrorToMessage(error)
        is LocationErrorState -> mapLocationErrorToMessage(error)
        is ProfileErrorState -> mapProfileErrorToMessage(error)
        is ErrorState.GenericError -> Res.string.error_something_went_wrong
        else -> throw IllegalArgumentException("Unknown error type: ${error::class.simpleName}")
    }
}